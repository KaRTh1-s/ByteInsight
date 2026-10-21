import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  RefreshCw,
  ShoppingCart,
  CheckCircle,
  Flame
} from 'lucide-react';
import { plannerService } from '../services/api';
import './MealPlanner.css';

const daysOfWeek = [
  { id: 1, label: 'Mon' },
  { id: 2, label: 'Tue' },
  { id: 3, label: 'Wed' },
  { id: 4, label: 'Thu' },
  { id: 5, label: 'Fri' },
  { id: 6, label: 'Sat' },
  { id: 7, label: 'Sun' }
];

function MealPlanner() {
  const navigate = useNavigate();
  const [currentPlan, setCurrentPlan] = useState(null);
  const [selectedDay, setSelectedDay] = useState(1);
  const [loading, setLoading] = useState(true);
  const [regenerating, setRegenerating] = useState(false);
  const [exportNotice, setExportNotice] = useState('');

  const loadPlan = async () => {
    try {
      setLoading(true);
      const plan = await plannerService.getCurrentPlan();
      setCurrentPlan(plan);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPlan();
  }, []);

  const handleRegenerate = async (planType = 'BALANCED') => {
    try {
      setRegenerating(true);
      setExportNotice('');
      const newPlan = await plannerService.generatePlan(planType, 7);
      setCurrentPlan(newPlan);
    } catch (err) {
      alert('Error regenerating plan: ' + err.message);
    } finally {
      setRegenerating(false);
    }
  };

  const handleExportShoppingList = async () => {
    if (!currentPlan) return;
    try {
      await plannerService.convertToShoppingList(currentPlan.id);
      setExportNotice('Meal plan ingredients exported to your Grocery List!');
      setTimeout(() => navigate('/shopping-list'), 1500);
    } catch (err) {
      alert('Error exporting shopping list: ' + err.message);
    }
  };

  const dayItems = currentPlan?.items?.filter(item => item.dayOfWeek === selectedDay) || [];
  const dayCalories = dayItems.reduce((sum, item) => sum + (item.calories || 0), 0);
  const dayProtein = dayItems.reduce((sum, item) => sum + (item.proteinG || 0), 0);
  const dayCost = dayItems.reduce((sum, item) => sum + (item.estimatedCost || 0), 0);

  if (loading) {
    return (
      <div className="planner-page">
        <div className="card text-center" style={{ padding: '3rem' }}>
          <div className="loading-spinner"></div>
          <p style={{ marginTop: '1rem', color: 'var(--text-muted)' }}>Generating your personalized meal plan...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="planner-page">
      {/* Top Banner */}
      <div className="planner-header-row card">
        <div>
          <span className="badge badge-verified">Intelligent Planner</span>
          <h1 className="planner-title">{currentPlan?.planName || 'Personalized 7-Day Meal Schedule'}</h1>
          <p className="planner-sub">
            Formulated to meet your daily target of {Math.round(currentPlan?.targetCaloriesPerDay || 2000)} kcal while strictly respecting dietary preferences and declared allergens.
          </p>
        </div>

        <div className="planner-cta-group">
          <button
            className="btn-secondary"
            onClick={() => handleRegenerate('BALANCED')}
            disabled={regenerating}
          >
            <RefreshCw size={16} className={regenerating ? 'spin-icon' : ''} /> Regenerate Plan
          </button>
          <button
            className="btn-primary"
            onClick={handleExportShoppingList}
          >
            <ShoppingCart size={16} /> Export to Grocery List
          </button>
        </div>
      </div>

      {exportNotice && (
        <div className="alert-banner alert-success">
          <CheckCircle size={18} /> {exportNotice}
        </div>
      )}

      {/* Days of Week Tab Strip */}
      <div className="days-strip card">
        <div className="days-buttons-row">
          {daysOfWeek.map((day) => (
            <button
              key={day.id}
              className={`day-tab-btn ${selectedDay === day.id ? 'active' : ''}`}
              onClick={() => setSelectedDay(day.id)}
            >
              <span className="day-name-abbr">{day.label}</span>
              <span className="day-number-label">Day {day.id}</span>
            </button>
          ))}
        </div>

        {/* Selected Day Stats Pill */}
        <div className="day-stats-pill">
          <span>Day {selectedDay} Totals: </span>
          <strong>{Math.round(dayCalories)} kcal</strong> &bull;{' '}
          <strong>{Math.round(dayProtein)}g protein</strong> &bull;{' '}
          <span className="text-success">${dayCost.toFixed(2)} est. cost</span>
        </div>
      </div>

      {/* Suggested Meals for Selected Day */}
      <div className="day-meals-grid">
        {['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'].map((mealType) => {
          const item = dayItems.find(it => it.mealType === mealType);

          return (
            <div key={mealType} className="card meal-plan-card">
              <div className="meal-plan-card-header">
                <div>
                  <span className="meal-type-pill">{mealType}</span>
                  <h3 className="plan-item-title">{item?.title || 'Planned Meal'}</h3>
                </div>
                <div className="plan-cal-badge">
                  <Flame size={16} className="text-warning" />
                  <span>{Math.round(item?.calories || 0)} kcal</span>
                </div>
              </div>

              <p className="plan-item-desc">{item?.description || 'Chef-crafted balanced recipe.'}</p>

              <div className="plan-item-macros">
                <span className="macro-chip">Protein: <strong>{item?.proteinG || 0}g</strong></span>
                <span className="macro-chip">Carbs: <strong>{item?.carbsG || 0}g</strong></span>
                <span className="macro-chip">Fat: <strong>{item?.fatG || 0}g</strong></span>
                <span className="macro-chip">Fiber: <strong>{item?.fiberG || 0}g</strong></span>
              </div>

              <div className="plan-item-footer">
                <span className="item-cost-text">Est. Cost: ${item?.estimatedCost?.toFixed(2) || '2.50'}</span>
                <button
                  className="btn-outline btn-sm"
                  onClick={() => navigate('/recipes')}
                >
                  View Ingredients & Recipe
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default MealPlanner;
