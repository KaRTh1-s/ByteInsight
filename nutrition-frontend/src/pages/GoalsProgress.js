import React, { useState, useEffect } from 'react';
import {
  Target,
  Scale,
  Award,
  Plus,
  Activity
} from 'lucide-react';
import { goalsService, profileService, dashboardService } from '../services/api';
import './GoalsProgress.css';

function GoalsProgress() {
  const [profile, setProfile] = useState(null);
  const [summary, setSummary] = useState(null);
  const [weightHistory, setWeightHistory] = useState([]);
  const [newWeight, setNewWeight] = useState('');
  const [weightNote, setWeightNote] = useState('');
  const [loading, setLoading] = useState(true);
  const [showLogModal, setShowLogModal] = useState(false);

  const loadProgressData = async () => {
    try {
      setLoading(true);
      const [profData, sumData, weightData] = await Promise.all([
        profileService.getProfile(),
        dashboardService.getSummary(),
        goalsService.getWeightHistory()
      ]);
      setProfile(profData);
      setSummary(sumData);
      setWeightHistory(weightData || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProgressData();
  }, []);

  const handleLogWeightSubmit = async (e) => {
    e.preventDefault();
    if (!newWeight) return;

    try {
      const today = new Date().toISOString().split('T')[0];
      await goalsService.logWeight(Number(newWeight), today, weightNote);
      setShowLogModal(false);
      setNewWeight('');
      setWeightNote('');
      loadProgressData();
    } catch (err) {
      alert('Error logging weight: ' + err.message);
    }
  };

  if (loading) {
    return (
      <div className="goals-page">
        <div className="card text-center" style={{ padding: '3rem' }}>
          <div className="loading-spinner"></div>
          <p style={{ marginTop: '1rem', color: 'var(--text-muted)' }}>Loading your progress and goals...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="goals-page">
      {/* Top Banner */}
      <div className="goals-header card">
        <div>
          <span className="badge badge-verified">Progress & Trajectory</span>
          <h1 className="goals-title">Goals, Consistency & Trends</h1>
          <p className="goals-sub">
            Track multi-day adherence to personalized targets, observe energy intake stability, and optionally monitor weight progression.
          </p>
        </div>
      </div>

      {/* Target Adherence Snapshot Cards */}
      <div className="goals-cards-grid">
        <div className="card goal-stat-card">
          <div className="g-header">
            <span className="g-label">Current Nutrition Goal</span>
            <Target size={20} className="text-primary-color" />
          </div>
          <span className="g-main-val">
            {profile?.goal?.replace(/_/g, ' ') || 'Maintain Weight'}
          </span>
          <span className="g-sub-text">
            Calorie target: {Math.round(profile?.targetCalories || 2000)} kcal/day
          </span>
        </div>

        <div className="card goal-stat-card">
          <div className="g-header">
            <span className="g-label">Logging Consistency</span>
            <Award size={20} className="text-warning" />
          </div>
          <span className="g-main-val text-warning">Active Streak</span>
          <span className="g-sub-text">
            Multi-day tracking delivers more meaningful intelligence than isolated meals
          </span>
        </div>

        <div className="card goal-stat-card">
          <div className="g-header">
            <span className="g-label">Basal Metabolic Floor</span>
            <Activity size={20} className="text-info" />
          </div>
          <span className="g-main-val">Mifflin-St Jeor</span>
          <span className="g-sub-text">
            Calculated from age ({profile?.age || 28}), height ({profile?.heightCm || 175}cm), weight ({profile?.weightKg || 70}kg)
          </span>
        </div>
      </div>

      {/* 7-Day Macro Adherence Breakdown */}
      <div className="card macro-trend-card">
        <h3 className="section-card-title">Recent Caloric Intake History</h3>
        <p className="section-card-sub">Daily totals recorded over the last 7 calendar days</p>

        <div className="intake-history-table-wrap">
          <table className="intake-history-table">
            <thead>
              <tr>
                <th>Day</th>
                <th>Energy Intake</th>
                <th>Target</th>
                <th>Adherence</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {summary?.trendLast7Days?.map((pt, i) => {
                const diff = pt.calories - pt.targetCalories;
                const isWithinRange = Math.abs(diff) <= 250;
                return (
                  <tr key={i}>
                    <td><strong>{pt.dayName} ({pt.date})</strong></td>
                    <td>{pt.calories > 0 ? `${pt.calories} kcal` : 'No logs recorded'}</td>
                    <td>{pt.targetCalories} kcal</td>
                    <td>
                      {pt.calories > 0 ? (
                        <span className={isWithinRange ? 'text-success' : 'text-warning'}>
                          {diff > 0 ? `+${diff}` : `${diff}`} kcal
                        </span>
                      ) : '-'}
                    </td>
                    <td>
                      {pt.calories > 0 ? (
                        <span className={`badge ${isWithinRange ? 'badge-verified' : 'badge-warning'}`}>
                          {isWithinRange ? 'Target Aligned' : diff > 0 ? 'Surplus' : 'Deficit'}
                        </span>
                      ) : (
                        <span className="badge badge-warning">Unlogged</span>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      {/* Optional Weight Progression Section */}
      <div className="card weight-progression-card">
        <div className="weight-header-row">
          <div>
            <h3 className="section-card-title">Self-Reported Weight Progression (Optional)</h3>
            <p className="section-card-sub">
              Weight fluctuates normally day-to-day due to water retention and glycogen stores. Look for multi-week trends.
            </p>
          </div>
          <button className="btn-primary btn-sm" onClick={() => setShowLogModal(true)}>
            <Plus size={15} /> Log Weight
          </button>
        </div>

        <div className="weight-history-stream">
          {weightHistory.length > 0 ? (
            <div className="weight-chips-row">
              {weightHistory.map((w) => (
                <div key={w.id} className="weight-entry-chip">
                  <span className="w-date">{w.logDate}</span>
                  <span className="w-num">{w.weightKg} kg</span>
                  {w.note && <span className="w-note">{w.note}</span>}
                </div>
              ))}
            </div>
          ) : (
            <div className="empty-weight-box">
              <Scale size={36} className="text-muted" />
              <p>No weight logs recorded yet. Weight tracking is purely optional and self-reported.</p>
            </div>
          )}
        </div>
      </div>

      {/* Weight Modal */}
      {showLogModal && (
        <div className="modal-backdrop">
          <div className="modal-card">
            <h3 className="modal-title">Record Weight Checkpoint</h3>
            <p className="modal-sub">Log today's self-measured body weight</p>

            <form onSubmit={handleLogWeightSubmit}>
              <div className="form-group">
                <label className="form-label">Body Weight (kg)</label>
                <input
                  type="number"
                  step="0.1"
                  className="form-input"
                  placeholder="e.g. 71.5"
                  value={newWeight}
                  onChange={(e) => setNewWeight(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Optional Note</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g. Post-workout, morning measurement"
                  value={weightNote}
                  onChange={(e) => setWeightNote(e.target.value)}
                />
              </div>

              <div className="modal-actions-row">
                <button
                  type="button"
                  className="btn-secondary"
                  onClick={() => setShowLogModal(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  Save Weight
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default GoalsProgress;
