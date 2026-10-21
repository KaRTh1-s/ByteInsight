import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Flame,
  Dumbbell,
  Wheat,
  Droplet,
  Sparkles,
  PlusCircle,
  Trash2,
  AlertTriangle,
  CheckCircle2,
  Calendar,
  ShieldAlert,
  ArrowUpRight
} from 'lucide-react';
import { dashboardService, journalService } from '../services/api';
import './Dashboard.css';

function Dashboard() {
  const navigate = useNavigate();
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);

  const loadDashboard = async (date) => {
    try {
      setLoading(true);
      setError('');
      const data = await dashboardService.getSummary(date);
      setSummary(data);
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to load dashboard data.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboard(selectedDate);
  }, [selectedDate]);

  const handleDeleteItem = async (mealId, itemId) => {
    try {
      await journalService.deleteItem(mealId, itemId);
      loadDashboard(selectedDate);
    } catch (err) {
      alert('Error removing item: ' + err.message);
    }
  };

  if (loading && !summary) {
    return (
      <div className="dashboard-loading-state">
        <div className="spinner-glow" />
        <p className="loading-label">Evaluating your nutrition intelligence...</p>
      </div>
    );
  }

  const caloriePct = summary && summary.targetCalories
    ? Math.min(100, Math.round((summary.totalCalories / summary.targetCalories) * 100))
    : 0;

  const proteinPct = summary && summary.targetProteinG
    ? Math.min(100, Math.round((summary.totalProteinG / summary.targetProteinG) * 100))
    : 0;

  const carbsPct = summary && summary.targetCarbsG
    ? Math.min(100, Math.round((summary.totalCarbsG / summary.targetCarbsG) * 100))
    : 0;

  const fatPct = summary && summary.targetFatG
    ? Math.min(100, Math.round((summary.totalFatG / summary.targetFatG) * 100))
    : 0;

  const fiberPct = summary && summary.targetFiberG
    ? Math.min(100, Math.round((summary.totalFiberG / summary.targetFiberG) * 100))
    : 0;

  return (
    <div className="dashboard-page">
      {/* Top Banner & Date Selector */}
      <div className="dashboard-header-row">
        <div>
          <h1 className="dash-title">Personalized Nutrition Intelligence</h1>
          <p className="dash-subtitle">
            Traceable nutrient tracking, evidence-based meal quality scoring, and gap detection.
          </p>
        </div>
        <div className="dash-date-picker-wrap">
          <Calendar size={18} className="date-icon" />
          <input
            type="date"
            className="dash-date-input"
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
          />
        </div>
      </div>

      {error && <div className="alert-banner alert-error">{error}</div>}

      {/* Main Nutrition Metrics Grid */}
      <div className="metrics-summary-grid">
        {/* Calories Card with Ring Gauge */}
        <div className="card calorie-metric-card">
          <div className="metric-header">
            <span className="metric-tag">Energy Intake</span>
            <Flame size={20} className="macro-icon energy-icon" />
          </div>
          <div className="calorie-display-wrap">
            <div className="calorie-main-number">
              <span className="big-cal-val">{Math.round(summary?.totalCalories || 0)}</span>
              <span className="cal-unit">kcal</span>
            </div>
            <div className="calorie-subtext">
              Target: <strong>{Math.round(summary?.targetCalories || 2000)} kcal</strong> &bull;{' '}
              <span className={summary?.remainingCalories <= 0 ? 'text-warning' : 'text-success'}>
                {Math.round(summary?.remainingCalories || 0)} remaining
              </span>
            </div>
          </div>
          <div className="progress-bar-track">
            <div
              className="progress-bar-fill energy-fill"
              style={{ width: `${caloriePct}%` }}
            />
          </div>
          <div className="card-footer-metric">
            <span>{caloriePct}% of daily energy target</span>
          </div>
        </div>

        {/* Protein Card */}
        <div className="card macro-metric-card">
          <div className="metric-header">
            <span className="metric-tag">Protein</span>
            <Dumbbell size={18} className="macro-icon protein-icon" />
          </div>
          <div className="macro-numbers">
            <span className="macro-curr-val">{Math.round(summary?.totalProteinG || 0)}g</span>
            <span className="macro-target-val">/ {Math.round(summary?.targetProteinG || 120)}g</span>
          </div>
          <div className="progress-bar-track">
            <div className="progress-bar-fill protein-fill" style={{ width: `${proteinPct}%` }} />
          </div>
          <div className="card-footer-metric">
            <span>{proteinPct}% target &bull; Satiety & Muscle repair</span>
          </div>
        </div>

        {/* Carbohydrates Card */}
        <div className="card macro-metric-card">
          <div className="metric-header">
            <span className="metric-tag">Carbohydrates</span>
            <Wheat size={18} className="macro-icon carbs-icon" />
          </div>
          <div className="macro-numbers">
            <span className="macro-curr-val">{Math.round(summary?.totalCarbsG || 0)}g</span>
            <span className="macro-target-val">/ {Math.round(summary?.targetCarbsG || 250)}g</span>
          </div>
          <div className="progress-bar-track">
            <div className="progress-bar-fill carbs-fill" style={{ width: `${carbsPct}%` }} />
          </div>
          <div className="card-footer-metric">
            <span>{carbsPct}% target &bull; Cellular glycogen</span>
          </div>
        </div>

        {/* Total Fat Card */}
        <div className="card macro-metric-card">
          <div className="metric-header">
            <span className="metric-tag">Total Fat</span>
            <Droplet size={18} className="macro-icon fat-icon" />
          </div>
          <div className="macro-numbers">
            <span className="macro-curr-val">{Math.round(summary?.totalFatG || 0)}g</span>
            <span className="macro-target-val">/ {Math.round(summary?.targetFatG || 70)}g</span>
          </div>
          <div className="progress-bar-track">
            <div className="progress-bar-fill fat-fill" style={{ width: `${fatPct}%` }} />
          </div>
          <div className="card-footer-metric">
            <span>{fatPct}% target &bull; Hormone & Vitamin absorption</span>
          </div>
        </div>
      </div>

      {/* Row: Meal Balance Score + Actionable Insights */}
      <div className="insights-and-score-row">
        {/* Meal Balance Score Card */}
        <div className="card balance-score-card">
          <div className="balance-header">
            <div>
              <h3 className="section-card-title">Daily Meal Quality & Balance</h3>
              <p className="section-card-sub">AMDR macro adherence, fiber density & sodium control</p>
            </div>
            <span className="badge badge-verified">Documented Formula</span>
          </div>

          <div className="balance-gauge-area">
            <div className="balance-score-circle">
              <span className="score-number">{summary?.dayBalanceScore || 0}</span>
              <span className="score-denom">/ 100</span>
            </div>
            <div className="balance-rating-info">
              <h4 className="rating-badge-title">{summary?.dayBalanceRating || 'Awaiting Logs'}</h4>
              <p className="rating-desc-text">
                {summary?.dayBalanceScore >= 80
                  ? 'Outstanding nutrient density and harmonious macro distribution.'
                  : summary?.dayBalanceScore >= 60
                  ? 'Healthy meal composition with minor opportunities for fiber or sodium optimization.'
                  : summary?.mealsLoggedCount > 0
                  ? 'Intake shows nutrient skew. Check the actionable suggestions below.'
                  : 'Log at least one meal to unlock your real-time dietary balance score.'}
              </p>
            </div>
          </div>

          {/* Micronutrient Chips */}
          <div className="micro-progress-chips">
            <div className="micro-chip">
              <span className="chip-name">Fiber</span>
              <span className="chip-val">{Math.round(summary?.totalFiberG || 0)} / {Math.round(summary?.targetFiberG || 30)}g ({fiberPct}%)</span>
            </div>
            <div className="micro-chip">
              <span className="chip-name">Sodium</span>
              <span className={`chip-val ${summary?.totalSodiumMg > 2300 ? 'text-warning' : ''}`}>
                {Math.round(summary?.totalSodiumMg || 0)} / {Math.round(summary?.targetSodiumMg || 2300)}mg
              </span>
            </div>
            <div className="micro-chip">
              <span className="chip-name">Potassium</span>
              <span className="chip-val">{Math.round(summary?.totalPotassiumMg || 0)} / {Math.round(summary?.targetPotassiumMg || 3400)}mg</span>
            </div>
            <div className="micro-chip">
              <span className="chip-name">Iron</span>
              <span className="chip-val">{Math.round(summary?.totalIronMg || 0)} / {Math.round(summary?.targetIronMg || 14)}mg</span>
            </div>
          </div>
        </div>

        {/* Personalized Explainable Insights */}
        <div className="card insights-card">
          <div className="insights-card-header">
            <div className="flex-center-gap">
              <Sparkles size={20} className="sparkle-icon" />
              <h3 className="section-card-title">Actionable Nutrition Guidance</h3>
            </div>
            <span className="insights-count-pill">{summary?.insights?.length || 0} alerts</span>
          </div>

          <div className="insights-stream-container">
            {summary?.insights && summary.insights.length > 0 ? (
              summary.insights.map((ins, idx) => (
                <div key={idx} className={`insight-alert-item alert-type-${ins.type?.toLowerCase()}`}>
                  <div className="alert-icon-col">
                    {ins.type === 'SUCCESS' && <CheckCircle2 size={18} className="text-success" />}
                    {ins.type === 'WARNING' && <AlertTriangle size={18} className="text-warning" />}
                    {ins.type === 'TIP' && <Sparkles size={18} className="text-info" />}
                    {ins.type === 'INFO' && <ShieldAlert size={18} className="text-muted" />}
                  </div>
                  <div className="alert-content-col">
                    <h5 className="alert-title-text">{ins.title}</h5>
                    <p className="alert-body-text">{ins.message}</p>
                    {ins.actionableSuggestion && (
                      <p className="alert-suggestion-text">
                        <strong>Suggestion:</strong> {ins.actionableSuggestion}
                      </p>
                    )}
                  </div>
                </div>
              ))
            ) : (
              <div className="empty-insights-box">
                <p>No intake alerts for today yet. Keep logging your meals to receive guidance.</p>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* 7-Day Intake Trend Interactive Chart */}
      <div className="card trend-chart-card">
        <div className="trend-header-row">
          <div>
            <h3 className="section-card-title">7-Day Energy & Macronutrient Trajectory</h3>
            <p className="section-card-sub">Historical comparison across consecutive logging days</p>
          </div>
          <button className="btn-outline btn-sm" onClick={() => navigate('/goals')}>
            Detailed Analytics <ArrowUpRight size={15} />
          </button>
        </div>

        <div className="trend-bars-container">
          {summary?.trendLast7Days?.map((point, index) => {
            const heightPct = Math.min(100, Math.round((point.calories / (point.targetCalories * 1.3)) * 100));
            const isTargetMet = point.calories >= point.targetCalories * 0.85 && point.calories <= point.targetCalories * 1.15;
            return (
              <div key={index} className="trend-bar-column">
                <div className="trend-bar-track">
                  <div
                    className={`trend-bar-bar ${isTargetMet ? 'bar-target-met' : 'bar-normal'}`}
                    style={{ height: `${Math.max(6, heightPct)}%` }}
                    title={`${point.date}: ${point.calories} kcal (Target: ${point.targetCalories} kcal)`}
                  />
                </div>
                <span className="trend-day-label">{point.dayName}</span>
                <span className="trend-cal-label">{point.calories > 0 ? `${point.calories}` : '-'}</span>
              </div>
            );
          })}
        </div>
      </div>

      {/* Today's Logged Meals Cards with Items */}
      <div className="logged-meals-section">
        <div className="meals-section-header">
          <div>
            <h3 className="section-title">Today's Meals Journal</h3>
            <p className="section-sub">Reviewed intake by category</p>
          </div>
          <button className="btn-primary" onClick={() => navigate('/journal')}>
            <PlusCircle size={17} /> Add Food to Journal
          </button>
        </div>

        <div className="meal-categories-grid">
          {['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'].map((mealCategory) => {
            const items = summary?.mealSections?.[mealCategory] || [];
            const categoryCalories = items.reduce((sum, it) => sum + (it.calories || 0), 0);
            const categoryProtein = items.reduce((sum, it) => sum + (it.proteinG || 0), 0);

            return (
              <div key={mealCategory} className="card meal-category-card">
                <div className="meal-cat-header">
                  <div>
                    <h4 className="meal-cat-title">{mealCategory}</h4>
                    <span className="meal-cat-stats">
                      {Math.round(categoryCalories)} kcal &bull; {Math.round(categoryProtein)}g protein
                    </span>
                  </div>
                  <button
                    className="quick-add-icon-btn"
                    onClick={() => navigate('/analyzer')}
                    title="Analyze and Add Food"
                  >
                    <PlusCircle size={18} />
                  </button>
                </div>

                <div className="meal-items-list">
                  {items.length > 0 ? (
                    items.map((item) => (
                      <div key={item.itemId} className="meal-item-row">
                        <div className="meal-item-info">
                          <span className="item-name-label">{item.foodName}</span>
                          <span className="item-portion-label">
                            {item.portion} {item.unit} &bull; {Math.round(item.calories)} kcal
                          </span>
                        </div>
                        <button
                          className="item-delete-btn"
                          onClick={() => handleDeleteItem(item.mealId, item.itemId)}
                          title="Remove item"
                        >
                          <Trash2 size={14} />
                        </button>
                      </div>
                    ))
                  ) : (
                    <div className="empty-meal-slot">
                      <p>No food logged for {mealCategory.toLowerCase()} yet.</p>
                      <button
                        className="btn-secondary btn-sm"
                        onClick={() => navigate('/journal')}
                      >
                        Log {mealCategory}
                      </button>
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
