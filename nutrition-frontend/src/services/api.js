import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor to automatically attach JWT token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('byteinsight_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => Promise.reject(error));

// Interceptor for handling 401 unauthenticated
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Don't auto-redirect on login or register endpoints
      const isAuthUrl = error.config.url.includes('/auth/login') || error.config.url.includes('/auth/register');
      if (!isAuthUrl) {
        localStorage.removeItem('byteinsight_token');
        localStorage.removeItem('byteinsight_user');
      }
    }
    return Promise.reject(error);
  }
);

export const authService = {
  async register(username, email, password) {
    const res = await api.post('/auth/register', { username, email, password });
    if (res.data && res.data.token) {
      localStorage.setItem('byteinsight_token', res.data.token);
      localStorage.setItem('byteinsight_user', res.data.username);
    }
    return res.data;
  },

  async login(username, password) {
    const res = await api.post('/auth/login', { username, password });
    if (res.data && res.data.token) {
      localStorage.setItem('byteinsight_token', res.data.token);
      localStorage.setItem('byteinsight_user', res.data.username);
    }
    return res.data;
  },

  logout() {
    localStorage.removeItem('byteinsight_token');
    localStorage.removeItem('byteinsight_user');
    localStorage.removeItem('loggedInUser');
  },

  getCurrentUser() {
    return localStorage.getItem('byteinsight_user') || localStorage.getItem('loggedInUser');
  },

  isAuthenticated() {
    return !!localStorage.getItem('byteinsight_token');
  },

  async getMe() {
    const res = await api.get('/auth/me');
    return res.data;
  }
};

export const profileService = {
  async getProfile() {
    const res = await api.get('/profile');
    return res.data;
  },

  async updateProfile(profileData) {
    const res = await api.put('/profile', profileData);
    return res.data;
  }
};

export const foodService = {
  // Backward compatible POST /api/nutrition
  async queryNutrition(query) {
    const res = await api.post('/nutrition', { query });
    return res.data;
  },

  async searchFoods(query = '') {
    const res = await api.get(`/foods/search?query=${encodeURIComponent(query)}`);
    return res.data;
  },

  async getFoodById(id) {
    const res = await api.get(`/foods/${id}`);
    return res.data;
  },

  async analyzeMeal(mealData) {
    const res = await api.post('/foods/analyze', mealData);
    return res.data;
  },

  async analyzeFoodImage(imageFile) {
    const formData = new FormData();
    formData.append('image', imageFile);
    const res = await api.post('/foods/analyze-image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return res.data;
  }
};

export const journalService = {
  async getMeals(date) {
    const params = date ? `?date=${date}` : '';
    const res = await api.get(`/meals${params}`);
    return res.data;
  },

  async addFoodToMeal(mealPayload) {
    const res = await api.post('/meals', mealPayload);
    return res.data;
  },

  async deleteItem(mealId, itemId) {
    const res = await api.delete(`/meals/${mealId}/items/${itemId}`);
    return res.data;
  },

  async deleteMeal(mealId) {
    const res = await api.delete(`/meals/${mealId}`);
    return res.data;
  },

  async duplicateDay(sourceDate, targetDate) {
    const res = await api.post(`/meals/duplicate?sourceDate=${sourceDate}&targetDate=${targetDate}`);
    return res.data;
  }
};

export const dashboardService = {
  async getSummary(date) {
    const params = date ? `?date=${date}` : '';
    const res = await api.get(`/dashboard/summary${params}`);
    return res.data;
  }
};

export const plannerService = {
  async getCurrentPlan() {
    const res = await api.get('/planner/current');
    return res.data;
  },

  async generatePlan(planType = 'BALANCED', days = 7) {
    const res = await api.post('/planner/generate', { planType, days });
    return res.data;
  },

  async convertToShoppingList(mealPlanId) {
    const res = await api.post('/planner/to-shopping-list', { mealPlanId });
    return res.data;
  }
};

export const recipeService = {
  async getRecipes(query = '', category = '') {
    const params = new URLSearchParams();
    if (query) params.append('query', query);
    if (category) params.append('category', category);
    const res = await api.get(`/recipes?${params.toString()}`);
    return res.data;
  },

  async getRecipeById(id) {
    const res = await api.get(`/recipes/${id}`);
    return res.data;
  }
};

export const shoppingListService = {
  async getList() {
    const res = await api.get('/shopping-list');
    return res.data;
  },

  async addItem(item) {
    const res = await api.post('/shopping-list', item);
    return res.data;
  },

  async toggleItem(id) {
    const res = await api.put(`/shopping-list/${id}/toggle`);
    return res.data;
  },

  async deleteItem(id) {
    const res = await api.delete(`/shopping-list/${id}`);
    return res.data;
  },

  async clearPurchased() {
    const res = await api.delete('/shopping-list/clear-purchased');
    return res.data;
  }
};

export const goalsService = {
  async getWeightHistory() {
    const res = await api.get('/goals/weight');
    return res.data;
  },

  async logWeight(weightKg, date, note) {
    const res = await api.post('/goals/weight', { weightKg, date, note });
    return res.data;
  }
};

export default api;
