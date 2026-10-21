import React, { useState, useEffect } from 'react';
import {
  CheckCircle2,
  AlertTriangle,
  Activity,
  Heart,
  Zap,
  Info
} from 'lucide-react';
import { dashboardService, profileService } from '../services/api';
import './NutritionInsights.css';

function NutritionInsights() {
  const [summary, setSummary] = useState(null);
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      dashboardService.getSummary(),
      profileService.getProfile()
    ])
      .then(([sumData, profData]) => {
        setSummary(sumData);
        setProfile(profData);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const totalCal = summary?.totalCalories || 0;
  const protCal = (summary?.totalProteinG || 0) * 4;
  const carbCal = (summary?.totalCarbsG || 0) * 4;
  const fatCal = (summary?.totalFatG || 0) * 9;
  const macroCal = Math.max(1, protCal + carbCal + fatCal);

  const protPct = Math.round((protCal / macroCal) * 100);
  const carbPct = Math.round((carbCal / macroCal) * 100);
  const fatPct = Math.round((fatCal / macroCal) * 100);

  const fiberG = summary?.totalFiberG || 0;
  const sodiumMg = summary?.totalSodiumMg || 0;
  const potassiumMg = summary?.totalPotassiumMg || 0;

  if (loading) {
    return (
      <div className="insights-page">
        <div className="card text-center" style={{ padding: '3rem' }}>
          <div className="loading-spinner"></div>
          <p style={{ marginTop: '1rem', color: 'var(--text-muted)' }}>Calculating dietary intelligence and AMDR compliance...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="insights-page">
      {/* Header Banner */}
      <div className="insights-header-banner card">
        <div className="insights-header-text">
          <span className="badge badge-verified">Explainable Nutrition Engine</span>
          <h1 className="insights-title">Personalized Dietary Intelligence</h1>
          <p className="insights-sub">
            Target: {profile?.goal?.replace(/_/g, ' ') || 'General Balance'} &bull; Contextual evaluation against established reference guidelines (AMDR & DRI).
          </p>
        </div>
        <div className="non-diagnostic-badge">
          <Info size={16} />
          <span>Non-clinical decision support &bull; Estimates based on recorded food logs</span>
        </div>
      </div>

      {/* AMDR Macronutrient Compliance Section */}
      <div className="card amdr-section-card">
        <div className="amdr-header">
          <div>
            <h3 className="section-card-title">Macronutrient Distribution (AMDR Compliance)</h3>
            <p className="section-card-sub">
              Distribution across {Math.round(totalCal)} kcal recorded intake vs Food and Nutrition Board standards
            </p>
          </div>
          <span className="badge badge-api">Current Logged Balance</span>
        </div>

        <div className="amdr-bars-grid">
          {/* Protein AMDR */}
          <div className="amdr-bar-card">
            <div className="amdr-bar-top">
              <span className="amdr-nutrient-name">Protein</span>
              <span className="amdr-val-pct">{protPct}% kcal</span>
            </div>
            <div className="amdr-track">
              <div
                className="amdr-fill"
                style={{ width: `${Math.min(100, protPct * 2)}%`, backgroundColor: 'var(--macro-protein)' }}
              />
            </div>
            <div className="amdr-footer">
              <span>Standard AMDR: 10% - 35%</span>
              <span className={protPct >= 15 && protPct <= 35 ? 'text-success' : 'text-warning'}>
                {protPct >= 15 && protPct <= 35 ? '✓ Compliant' : 'Sub-optimal'}
              </span>
            </div>
          </div>

          {/* Carbs AMDR */}
          <div className="amdr-bar-card">
            <div className="amdr-bar-top">
              <span className="amdr-nutrient-name">Carbohydrates</span>
              <span className="amdr-val-pct">{carbPct}% kcal</span>
            </div>
            <div className="amdr-track">
              <div
                className="amdr-fill"
                style={{ width: `${Math.min(100, carbPct * 1.5)}%`, backgroundColor: 'var(--macro-carbs)' }}
              />
            </div>
            <div className="amdr-footer">
              <span>Standard AMDR: 45% - 65%</span>
              <span className={carbPct >= 40 && carbPct <= 65 ? 'text-success' : 'text-warning'}>
                {carbPct >= 40 && carbPct <= 65 ? '✓ Compliant' : 'Outside optimal range'}
              </span>
            </div>
          </div>

          {/* Fat AMDR */}
          <div className="amdr-bar-card">
            <div className="amdr-bar-top">
              <span className="amdr-nutrient-name">Total Fat</span>
              <span className="amdr-val-pct">{fatPct}% kcal</span>
            </div>
            <div className="amdr-track">
              <div
                className="amdr-fill"
                style={{ width: `${Math.min(100, fatPct * 2.5)}%`, backgroundColor: 'var(--macro-fat)' }}
              />
            </div>
            <div className="amdr-footer">
              <span>Standard AMDR: 20% - 35%</span>
              <span className={fatPct >= 20 && fatPct <= 35 ? 'text-success' : 'text-warning'}>
                {fatPct >= 20 && fatPct <= 35 ? '✓ Compliant' : 'Elevated'}
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Critical Nutrient Indicators Grid */}
      <div className="indicators-grid">
        {/* Fiber Sufficiency Card */}
        <div className="card indicator-card">
          <div className="indicator-icon-wrap fiber-icon-wrap">
            <Zap size={22} className="indicator-icon" />
          </div>
          <h4 className="indicator-title">Dietary Fiber Density</h4>
          <div className="indicator-metric-row">
            <span className="indicator-num">{Math.round(fiberG * 10) / 10}g</span>
            <span className="indicator-target">/ {Math.round(summary?.targetFiberG || 30)}g target</span>
          </div>
          <p className="indicator-explanation">
            {fiberG >= 25
              ? 'Excellent fiber intake. Soluble and insoluble fibers support sustained satiety, cholesterol regulation, and microbiome diversity.'
              : 'Recorded fiber is below optimal metabolic thresholds. Prioritize unpeeled fruits, legumes (dal/chickpeas), and seeds.'}
          </p>
          <div className="indicator-action-tip">
            <strong>Food Fix:</strong> 1 cup of cooked lentils (+15g fiber) or 2 tbsp chia seeds (+10g fiber).
          </div>
        </div>

        {/* Sodium to Potassium Ratio Card */}
        <div className="card indicator-card">
          <div className="indicator-icon-wrap heart-icon-wrap">
            <Heart size={22} className="indicator-icon" />
          </div>
          <h4 className="indicator-title">Electrolyte & Sodium Balance</h4>
          <div className="indicator-metric-row">
            <span className="indicator-num">{Math.round(sodiumMg)}mg Na</span>
            <span className="indicator-target">vs {Math.round(potassiumMg)}mg K</span>
          </div>
          <p className="indicator-explanation">
            {potassiumMg > sodiumMg && sodiumMg <= 2300
              ? 'Favorable potassium-to-sodium ratio. Higher dietary potassium relative to sodium supports optimal arterial wall relaxation.'
              : 'Sodium intake is elevated relative to potassium. Balance processed foods by increasing dietary potassium from leafy greens.'}
          </p>
          <div className="indicator-action-tip">
            <strong>Food Fix:</strong> Medium banana (422mg K), baked sweet potato (438mg K), or spinach.
          </div>
        </div>

        {/* Micronutrient Diversity Card */}
        <div className="card indicator-card">
          <div className="indicator-icon-wrap micro-icon-wrap">
            <Activity size={22} className="indicator-icon" />
          </div>
          <h4 className="indicator-title">Calcium & Iron Adequacy</h4>
          <div className="indicator-metric-row">
            <span className="indicator-num">{Math.round(summary?.totalIronMg || 0)}mg Fe</span>
            <span className="indicator-target">&bull; {Math.round(summary?.totalCalciumMg || 0)}mg Ca</span>
          </div>
          <p className="indicator-explanation">
            Iron supports hemoglobin oxygen transport, while calcium maintains skeletal mineralization.
            Pair non-heme plant iron (spinach, beans) with Vitamin C for optimal absorption.
          </p>
          <div className="indicator-action-tip">
            <strong>Food Fix:</strong> Squeeze fresh lemon juice over spinach and legume curries.
          </div>
        </div>
      </div>

      {/* Actionable Guidance List */}
      <div className="card actionable-guidance-card">
        <h3 className="section-card-title">Contextual Recommendations</h3>
        <p className="section-card-sub">Actionable steps formulated specifically from your latest dietary journal</p>

        <div className="guidance-list">
          {summary?.insights && summary.insights.length > 0 ? (
            summary.insights.map((ins, i) => (
              <div key={i} className="guidance-item">
                <div className="guidance-icon-col">
                  {ins.type === 'SUCCESS' ? (
                    <CheckCircle2 size={20} className="text-success" />
                  ) : (
                    <AlertTriangle size={20} className="text-warning" />
                  )}
                </div>
                <div className="guidance-body-col">
                  <h4 className="guidance-item-title">{ins.title}</h4>
                  <p className="guidance-item-msg">{ins.message}</p>
                  {ins.actionableSuggestion && (
                    <p className="guidance-item-suggestion">
                      <strong>Take Action:</strong> {ins.actionableSuggestion}
                    </p>
                  )}
                </div>
              </div>
            ))
          ) : (
            <div className="empty-insights-box">
              <p>Log your meals in the Meal Journal or Food Analyzer to generate personalized insights.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default NutritionInsights;
