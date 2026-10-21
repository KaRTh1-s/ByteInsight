import React, { useState, useEffect } from 'react';
import {
  CheckCircle2,
  Calculator,
  Save
} from 'lucide-react';
import { profileService } from '../services/api';
import './ProfileSettings.css';

const commonAllergens = [
  'Peanuts',
  'Dairy',
  'Gluten',
  'Shellfish',
  'Soy',
  'Eggs',
  'Tree Nuts'
];

function ProfileSettings() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [saveMessage, setSaveMessage] = useState('');

  // Form State
  const [age, setAge] = useState(28);
  const [sex, setSex] = useState('MALE');
  const [heightCm, setHeightCm] = useState(175);
  const [weightKg, setWeightKg] = useState(70);
  const [activityLevel, setActivityLevel] = useState('MODERATELY_ACTIVE');
  const [goal, setGoal] = useState('MAINTAIN_WEIGHT');
  const [dietaryPreference, setDietaryPreference] = useState('ALL');
  const [allergiesList, setAllergiesList] = useState([]);
  const [dislikedFoods, setDislikedFoods] = useState('');
  const [budgetPerDay, setBudgetPerDay] = useState(15);
  const [preferredCuisines, setPreferredCuisines] = useState('Global, Mediterranean, Indian');
  const [mealsPerDay, setMealsPerDay] = useState(3);
  const [customTargets, setCustomTargets] = useState(false);
  const [customCal, setCustomCal] = useState(2000);
  const [customProt, setCustomProt] = useState(120);

  const loadProfile = async () => {
    try {
      setLoading(true);
      const data = await profileService.getProfile();
      setProfile(data);

      if (data) {
        setAge(data.age || 28);
        setSex(data.sex || 'MALE');
        setHeightCm(data.heightCm || 175);
        setWeightKg(data.weightKg || 70);
        setActivityLevel(data.activityLevel || 'MODERATELY_ACTIVE');
        setGoal(data.goal || 'MAINTAIN_WEIGHT');
        setDietaryPreference(data.dietaryPreference || 'ALL');
        setAllergiesList(data.allergies ? data.allergies.split(',').map(s => s.trim()).filter(Boolean) : []);
        setDislikedFoods(data.dislikedFoods || '');
        setBudgetPerDay(data.budgetPerDay || 15);
        setPreferredCuisines(data.preferredCuisines || 'Global, Mediterranean, Indian');
        setMealsPerDay(data.mealsPerDay || 3);
        setCustomTargets(!!data.customTargets);
        setCustomCal(data.targetCalories || 2000);
        setCustomProt(data.targetProteinG || 120);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProfile();
  }, []);

  const toggleAllergen = (allergen) => {
    setAllergiesList(prev =>
      prev.includes(allergen)
        ? prev.filter(a => a !== allergen)
        : [...prev, allergen]
    );
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setSaveMessage('');

    try {
      const updated = await profileService.updateProfile({
        age: Number(age),
        sex,
        heightCm: Number(heightCm),
        weightKg: Number(weightKg),
        activityLevel,
        goal,
        dietaryPreference,
        allergies: allergiesList.join(', '),
        dislikedFoods,
        budgetPerDay: Number(budgetPerDay),
        preferredCuisines,
        mealsPerDay: Number(mealsPerDay),
        customTargets,
        targetCalories: customTargets ? Number(customCal) : undefined,
        targetProteinG: customTargets ? Number(customProt) : undefined
      });
      setProfile(updated);
      setSaveMessage('Profile and nutrition targets successfully updated & recalculated!');
    } catch (err) {
      alert('Error updating profile: ' + err.message);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="profile-page">
        <div className="card text-center" style={{ padding: '3rem' }}>
          <div className="loading-spinner"></div>
          <p style={{ marginTop: '1rem', color: 'var(--text-muted)' }}>Loading your profile and nutrition parameters...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-page">
      {/* Top Banner */}
      <div className="profile-header card">
        <div>
          <span className="badge badge-verified">Configurable Context</span>
          <h1 className="profile-title">Personal Profile & Dietary Preferences</h1>
          <p className="profile-sub">
            Your physiological parameters dictate personalized caloric floors, acceptable macronutrient distributions, and allergen filtering.
          </p>
        </div>
      </div>

      {saveMessage && (
        <div className="alert-banner alert-success">
          <CheckCircle2 size={18} /> {saveMessage}
        </div>
      )}

      <form onSubmit={handleSave} className="profile-form-layout">
        {/* Left Column: Form Fields */}
        <div className="profile-form-column">
          {/* Biometrics Card */}
          <div className="card">
            <h3 className="section-card-title">1. Biometrics & Demographics</h3>
            <p className="section-card-sub">Used deterministically in the Mifflin-St Jeor basal metabolic calculation</p>

            <div className="form-grid-2 mt-3">
              <div className="form-group">
                <label className="form-label">Age (years)</label>
                <input
                  type="number"
                  min="16"
                  max="120"
                  className="form-input"
                  value={age}
                  onChange={(e) => setAge(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Biological Sex</label>
                <select
                  className="form-select"
                  value={sex}
                  onChange={(e) => setSex(e.target.value)}
                >
                  <option value="MALE">Male (+5 kcal BMR offset)</option>
                  <option value="FEMALE">Female (-161 kcal BMR offset)</option>
                  <option value="OTHER">Other / Non-Binary</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Height (cm)</label>
                <input
                  type="number"
                  min="100"
                  max="250"
                  className="form-input"
                  value={heightCm}
                  onChange={(e) => setHeightCm(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Body Weight (kg)</label>
                <input
                  type="number"
                  min="30"
                  max="300"
                  step="0.5"
                  className="form-input"
                  value={weightKg}
                  onChange={(e) => setWeightKg(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Physical Activity Level</label>
              <select
                className="form-select"
                value={activityLevel}
                onChange={(e) => setActivityLevel(e.target.value)}
              >
                <option value="SEDENTARY">Sedentary (Little or no exercise &bull; 1.2x TDEE)</option>
                <option value="LIGHTLY_ACTIVE">Lightly Active (Exercise 1-3 days/week &bull; 1.375x TDEE)</option>
                <option value="MODERATELY_ACTIVE">Moderately Active (Exercise 3-5 days/week &bull; 1.55x TDEE)</option>
                <option value="VERY_ACTIVE">Very Active (Hard exercise 6-7 days/week &bull; 1.725x TDEE)</option>
                <option value="EXTRA_ACTIVE">Extra Active (Intense training / manual labor &bull; 1.9x TDEE)</option>
              </select>
            </div>
          </div>

          {/* Nutrition Goal & Dietary Philosophy */}
          <div className="card">
            <h3 className="section-card-title">2. Dietary Strategy & Goals</h3>
            <p className="section-card-sub">Configures caloric deficit/surplus and macro targeting</p>

            <div className="form-grid-2 mt-3">
              <div className="form-group">
                <label className="form-label">Primary Nutrition Goal</label>
                <select
                  className="form-select"
                  value={goal}
                  onChange={(e) => setGoal(e.target.value)}
                >
                  <option value="MAINTAIN_WEIGHT">Maintain Weight (Caloric Equilibrium)</option>
                  <option value="LOSE_WEIGHT">Lose Weight (Safe 450 kcal Deficit)</option>
                  <option value="GAIN_WEIGHT">Gain Lean Mass (Controlled 350 kcal Surplus)</option>
                  <option value="IMPROVE_HEALTH">Improve Health & Micronutrient Density</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Dietary Preference</label>
                <select
                  className="form-select"
                  value={dietaryPreference}
                  onChange={(e) => setDietaryPreference(e.target.value)}
                >
                  <option value="ALL">Omnivore / All Foods</option>
                  <option value="VEGETARIAN">Vegetarian (Plant-based + Dairy & Eggs)</option>
                  <option value="VEGAN">100% Vegan (Strictly Plant-based)</option>
                  <option value="PESCATARIAN">Pescatarian (Vegetarian + Seafood)</option>
                  <option value="KETO">Ketogenic (Very Low Carb, High Fat)</option>
                  <option value="MEDITERRANEAN">Mediterranean (Whole foods, Healthy Fats)</option>
                </select>
              </div>
            </div>

            {/* Allergies Toggle Chips */}
            <div className="form-group">
              <label className="form-label">Declared Food Allergens & Exclusions</label>
              <p className="field-hint-text">
                Excluded automatically across the Meal Planner and highlighted with warning badges during Food Analysis.
              </p>
              <div className="allergens-chips-row">
                {commonAllergens.map((allergen) => {
                  const isSelected = allergiesList.includes(allergen);
                  return (
                    <button
                      type="button"
                      key={allergen}
                      className={`allergen-toggle-btn ${isSelected ? 'active' : ''}`}
                      onClick={() => toggleAllergen(allergen)}
                    >
                      {isSelected ? '✓ ' : '+ '} {allergen}
                    </button>
                  );
                })}
              </div>
            </div>

            <div className="form-grid-2">
              <div className="form-group">
                <label className="form-label">Daily Grocery Budget ($)</label>
                <input
                  type="number"
                  min="5"
                  step="1"
                  className="form-input"
                  value={budgetPerDay}
                  onChange={(e) => setBudgetPerDay(e.target.value)}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Meals Per Day</label>
                <input
                  type="number"
                  min="2"
                  max="6"
                  className="form-input"
                  value={mealsPerDay}
                  onChange={(e) => setMealsPerDay(e.target.value)}
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Food Dislikes / Ingredients to Avoid</label>
              <input
                type="text"
                placeholder="e.g. Cilantro, Mushrooms, Bell peppers"
                className="form-input"
                value={dislikedFoods}
                onChange={(e) => setDislikedFoods(e.target.value)}
              />
            </div>
          </div>

          <button type="submit" className="btn-primary btn-block btn-lg" disabled={saving}>
            <Save size={18} /> {saving ? 'Recalculating & Saving...' : 'Save Profile & Update Targets'}
          </button>
        </div>

        {/* Right Column: Calculated Nutrition Targets Card */}
        <div className="profile-targets-column">
          <div className="card targets-summary-card">
            <div className="targets-header">
              <div className="flex-center-gap">
                <Calculator size={20} className="text-primary-color" />
                <h3 className="section-card-title">Calculated Nutrition Profile</h3>
              </div>
              <span className="badge badge-verified">Mifflin-St Jeor</span>
            </div>

            <div className="cal-target-display-box">
              <span className="cal-target-title">Calculated Energy Target</span>
              <span className="cal-target-number">{Math.round(profile?.targetCalories || 2000)}</span>
              <span className="cal-target-unit">kcal / day</span>
            </div>

            <div className="macro-targets-list">
              <div className="macro-target-row">
                <span className="m-name">Daily Protein</span>
                <span className="m-val text-warning">{Math.round(profile?.targetProteinG || 120)}g</span>
              </div>
              <div className="macro-target-row">
                <span className="m-name">Daily Carbohydrates</span>
                <span className="m-val text-info">{Math.round(profile?.targetCarbsG || 250)}g</span>
              </div>
              <div className="macro-target-row">
                <span className="m-name">Daily Total Fat</span>
                <span className="m-val">{Math.round(profile?.targetFatG || 70)}g</span>
              </div>
              <div className="macro-target-row">
                <span className="m-name">Daily Dietary Fiber</span>
                <span className="m-val text-success">{Math.round(profile?.targetFiberG || 30)}g</span>
              </div>
              <div className="macro-target-row">
                <span className="m-name">Daily Sodium Ceiling</span>
                <span className="m-val">{Math.round(profile?.targetSodiumMg || 2300)}mg</span>
              </div>
              <div className="macro-target-row">
                <span className="m-name">Estimated Daily Water</span>
                <span className="m-val">{Math.round(profile?.targetWaterMl || 2500)} ml</span>
              </div>
            </div>

            <div className="formula-explanation-box">
              <span className="formula-label">Methodology & Assumptions</span>
              <p>
                Calculations use the Mifflin-St Jeor equation to estimate Basal Metabolic Rate (BMR), multiplied by your physical activity factor to determine Total Daily Energy Expenditure (TDEE).
              </p>
              <p>
                Macronutrients adhere to the Acceptable Macronutrient Distribution Ranges (AMDR), scaled to your goal without unsafe caloric restrictions.
              </p>
            </div>
          </div>
        </div>
      </form>
    </div>
  );
}

export default ProfileSettings;
