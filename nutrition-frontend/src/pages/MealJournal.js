import React, { useState, useEffect } from 'react';
import {
  Calendar,
  ChevronLeft,
  ChevronRight,
  Plus,
  Trash2,
  Copy
} from 'lucide-react';
import { journalService, foodService, dashboardService } from '../services/api';
import './MealJournal.css';

function MealJournal() {
  const [currentDate, setCurrentDate] = useState(new Date().toISOString().split('T')[0]);
  const [meals, setMeals] = useState([]);
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  // Add Item Modal
  const [showAddModal, setShowAddModal] = useState(false);
  const [targetCategory, setTargetCategory] = useState('LUNCH');
  const [searchFoodQuery, setSearchFoodQuery] = useState('');
  const [foodOptions, setFoodOptions] = useState([]);
  const [selectedFood, setSelectedFood] = useState(null);
  const [foodPortion, setFoodPortion] = useState(1);
  const [foodUnit, setFoodUnit] = useState('serving');

  // Duplicate Day Modal
  const [showDupModal, setShowDupModal] = useState(false);
  const [sourceDupDate, setSourceDupDate] = useState('');

  const loadJournalData = async (date) => {
    setLoading(true);
    try {
      const [mealsData, sumData] = await Promise.all([
        journalService.getMeals(date),
        dashboardService.getSummary(date)
      ]);
      setMeals(mealsData || []);
      setSummary(sumData);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadJournalData(currentDate);
  }, [currentDate]);

  const changeDateBy = (days) => {
    const d = new Date(currentDate);
    d.setDate(d.getDate() + days);
    setCurrentDate(d.toISOString().split('T')[0]);
  };

  // Search foods for modal
  const handleFoodSearch = async (val) => {
    setSearchFoodQuery(val);
    if (val.trim().length < 2) {
      setFoodOptions([]);
      return;
    }
    try {
      const results = await foodService.searchFoods(val);
      setFoodOptions(results.slice(0, 6));
    } catch (err) {
      console.error(err);
    }
  };

  const handleSelectOption = (food) => {
    setSelectedFood(food);
    setSearchFoodQuery(food.name);
    setFoodUnit(food.servingUnit || 'serving');
    setFoodOptions([]);
  };

  const handleAddItemSubmit = async (e) => {
    e.preventDefault();
    if (!searchFoodQuery.trim()) return;

    try {
      let cal = 0, prot = 0, fat = 0, carbs = 0, fiber = 0, sod = 0, cost = 1.5;
      if (selectedFood) {
        cal = selectedFood.calories * foodPortion;
        prot = selectedFood.proteinG * foodPortion;
        fat = selectedFood.fatTotalG * foodPortion;
        carbs = selectedFood.carbsTotalG * foodPortion;
        fiber = selectedFood.fiberG * foodPortion;
        sod = selectedFood.sodiumMg * foodPortion;
        cost = selectedFood.costPerServing * foodPortion;
      }

      await journalService.addFoodToMeal({
        date: currentDate,
        mealType: targetCategory,
        foodName: searchFoodQuery.trim(),
        portion: Number(foodPortion) || 1,
        unit: foodUnit,
        calories: cal,
        protein: prot,
        fat: fat,
        carbs: carbs,
        fiber: fiber,
        sodium: sod,
        cost: cost
      });

      setShowAddModal(false);
      setSearchFoodQuery('');
      setSelectedFood(null);
      setFoodPortion(1);
      loadJournalData(currentDate);
    } catch (err) {
      alert('Error adding item: ' + err.message);
    }
  };

  const handleDeleteItem = async (mealId, itemId) => {
    try {
      await journalService.deleteItem(mealId, itemId);
      loadJournalData(currentDate);
    } catch (err) {
      alert('Error deleting item: ' + err.message);
    }
  };

  const handleDeleteMeal = async (mealId) => {
    if (!window.confirm('Delete this entire meal category?')) return;
    try {
      await journalService.deleteMeal(mealId);
      loadJournalData(currentDate);
    } catch (err) {
      alert('Error deleting meal: ' + err.message);
    }
  };

  const handleDuplicateSubmit = async (e) => {
    e.preventDefault();
    if (!sourceDupDate) return;
    try {
      await journalService.duplicateDay(sourceDupDate, currentDate);
      setShowDupModal(false);
      loadJournalData(currentDate);
    } catch (err) {
      alert('Error duplicating meals: ' + err.message);
    }
  };

  const categories = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];

  if (loading && meals.length === 0 && !summary) {
    return (
      <div className="journal-page">
        <div className="card text-center" style={{ padding: '3rem' }}>
          <div className="loading-spinner"></div>
          <p style={{ marginTop: '1rem', color: 'var(--text-muted)' }}>Loading your meal journal...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="journal-page">
      {/* Date Header & Action Bar */}
      <div className="journal-nav-bar card">
        <div className="date-nav-controls">
          <button className="date-arrow-btn" onClick={() => changeDateBy(-1)} title="Previous Day">
            <ChevronLeft size={20} />
          </button>
          <div className="current-date-box">
            <Calendar size={18} className="text-primary-color" />
            <input
              type="date"
              className="journal-date-input"
              value={currentDate}
              onChange={(e) => setCurrentDate(e.target.value)}
            />
          </div>
          <button className="date-arrow-btn" onClick={() => changeDateBy(1)} title="Next Day">
            <ChevronRight size={20} />
          </button>
        </div>

        <div className="journal-actions-row">
          <button
            className="btn-secondary btn-sm"
            onClick={() => {
              const yesterday = new Date(currentDate);
              yesterday.setDate(yesterday.getDate() - 1);
              setSourceDupDate(yesterday.toISOString().split('T')[0]);
              setShowDupModal(true);
            }}
          >
            <Copy size={15} /> Duplicate from Another Day
          </button>
        </div>
      </div>

      {/* Daily Nutrient Rollup Strip */}
      {summary && (
        <div className="card journal-rollup-card">
          <div className="rollup-item">
            <span className="rollup-label">Energy</span>
            <span className="rollup-val">{Math.round(summary.totalCalories)} / {Math.round(summary.targetCalories)} kcal</span>
          </div>
          <div className="rollup-divider" />
          <div className="rollup-item">
            <span className="rollup-label">Protein</span>
            <span className="rollup-val">{Math.round(summary.totalProteinG)} / {Math.round(summary.targetProteinG)} g</span>
          </div>
          <div className="rollup-divider" />
          <div className="rollup-item">
            <span className="rollup-label">Carbs</span>
            <span className="rollup-val">{Math.round(summary.totalCarbsG)} / {Math.round(summary.targetCarbsG)} g</span>
          </div>
          <div className="rollup-divider" />
          <div className="rollup-item">
            <span className="rollup-label">Fat</span>
            <span className="rollup-val">{Math.round(summary.totalFatG)} / {Math.round(summary.targetFatG)} g</span>
          </div>
          <div className="rollup-divider" />
          <div className="rollup-item">
            <span className="rollup-label">Fiber</span>
            <span className="rollup-val">{Math.round(summary.totalFiberG)} g</span>
          </div>
          <div className="rollup-divider" />
          <div className="rollup-item">
            <span className="rollup-label">Est. Cost</span>
            <span className="rollup-val text-success">${summary.estimatedDailyCost}</span>
          </div>
        </div>
      )}

      {/* Meal Categories Sections */}
      <div className="journal-meals-column">
        {categories.map((cat) => {
          const mealEntry = meals.find(m => m.mealType === cat);
          const items = mealEntry?.items || [];
          const catCal = items.reduce((s, it) => s + (it.calories || 0), 0);
          const catProt = items.reduce((s, it) => s + (it.proteinG || 0), 0);
          const catCarbs = items.reduce((s, it) => s + (it.carbsG || 0), 0);
          const catFat = items.reduce((s, it) => s + (it.fatG || 0), 0);

          return (
            <div key={cat} className="card meal-section-card">
              <div className="meal-section-header">
                <div>
                  <h3 className="meal-section-title">{cat}</h3>
                  <span className="meal-section-summary">
                    {Math.round(catCal)} kcal &bull; {Math.round(catProt)}g P &bull; {Math.round(catCarbs)}g C &bull; {Math.round(catFat)}g F
                  </span>
                </div>
                <div className="flex-center-gap">
                  {mealEntry && (
                    <button
                      className="btn-danger btn-sm"
                      onClick={() => handleDeleteMeal(mealEntry.id)}
                      title="Delete category"
                    >
                      <Trash2 size={14} /> Clear {cat}
                    </button>
                  )}
                  <button
                    className="btn-primary btn-sm"
                    onClick={() => {
                      setTargetCategory(cat);
                      setShowAddModal(true);
                    }}
                  >
                    <Plus size={15} /> Add Food
                  </button>
                </div>
              </div>

              {items.length > 0 ? (
                <div className="table-container">
                  <table className="journal-items-table">
                    <thead>
                      <tr>
                        <th>Food Item</th>
                        <th>Portion</th>
                        <th>Calories</th>
                        <th>Protein</th>
                        <th>Carbs</th>
                        <th>Fat</th>
                        <th>Fiber</th>
                        <th>Sodium</th>
                        <th>Cost</th>
                        <th>Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {items.map((it) => (
                        <tr key={it.id}>
                          <td><strong>{it.foodName}</strong></td>
                          <td>{it.portion} {it.unit}</td>
                          <td>{Math.round(it.calories)} kcal</td>
                          <td>{Math.round(it.proteinG)}g</td>
                          <td>{Math.round(it.carbsG)}g</td>
                          <td>{Math.round(it.fatG)}g</td>
                          <td>{it.fiberG}g</td>
                          <td>{Math.round(it.sodiumMg)}mg</td>
                          <td>${it.cost ? it.cost.toFixed(2) : '1.00'}</td>
                          <td>
                            <button
                              className="item-delete-btn"
                              onClick={() => handleDeleteItem(mealEntry.id, it.id)}
                              title="Delete food entry"
                            >
                              <Trash2 size={14} />
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <div className="empty-category-notice">
                  <p>No food recorded for {cat.toLowerCase()}. Click "+ Add Food" to log your meal.</p>
                </div>
              )}
            </div>
          );
        })}
      </div>

      {/* Add Food Modal */}
      {showAddModal && (
        <div className="modal-backdrop">
          <div className="modal-card">
            <h3 className="modal-title">Log Food to {targetCategory}</h3>
            <p className="modal-sub">Search our verified nutrition database or enter food name</p>

            <form onSubmit={handleAddItemSubmit}>
              <div className="form-group search-relative">
                <label className="form-label">Food or Ingredient</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g. Rolled Oats, Paneer, Chicken Breast..."
                  value={searchFoodQuery}
                  onChange={(e) => handleFoodSearch(e.target.value)}
                  required
                />
                {foodOptions.length > 0 && (
                  <div className="modal-search-dropdown">
                    {foodOptions.map((f) => (
                      <div
                        key={f.id}
                        className="modal-search-item"
                        onClick={() => handleSelectOption(f)}
                      >
                        <span className="food-name-strong">{f.name}</span>
                        <span className="food-sub-info">
                          {Math.round(f.calories)} kcal &bull; {f.servingUnit} ({f.source})
                        </span>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              <div className="form-row-2">
                <div className="form-group">
                  <label className="form-label">Portion</label>
                  <input
                    type="number"
                    min="0.1"
                    step="0.1"
                    className="form-input"
                    value={foodPortion}
                    onChange={(e) => setFoodPortion(e.target.value)}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Unit</label>
                  <select
                    className="form-select"
                    value={foodUnit}
                    onChange={(e) => setFoodUnit(e.target.value)}
                  >
                    <option value="serving">serving</option>
                    <option value="g">grams (g)</option>
                    <option value="cup">cup</option>
                    <option value="piece">piece</option>
                    <option value="oz">oz</option>
                    <option value="tbsp">tbsp</option>
                  </select>
                </div>
              </div>

              <div className="modal-actions-row">
                <button
                  type="button"
                  className="btn-secondary"
                  onClick={() => setShowAddModal(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  Save to Journal
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Duplicate Day Modal */}
      {showDupModal && (
        <div className="modal-backdrop">
          <div className="modal-card">
            <h3 className="modal-title">Duplicate Day Meals</h3>
            <p className="modal-sub">Copy all logged meals from a previous date into {currentDate}</p>

            <form onSubmit={handleDuplicateSubmit}>
              <div className="form-group">
                <label className="form-label">Source Date</label>
                <input
                  type="date"
                  className="form-input"
                  value={sourceDupDate}
                  onChange={(e) => setSourceDupDate(e.target.value)}
                  required
                />
              </div>

              <div className="modal-actions-row">
                <button
                  type="button"
                  className="btn-secondary"
                  onClick={() => setShowDupModal(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  Duplicate Meals
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default MealJournal;
