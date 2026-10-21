import React, { useState, useEffect } from 'react';
import {
  Search,
  Plus,
  Trash2,
  Camera,
  Layers,
  ArrowRight,
  CheckCircle,
  AlertCircle,
  Repeat,
  Scale,
  UploadCloud,
  Info
} from 'lucide-react';
import { foodService, journalService } from '../services/api';
import './FoodAnalyzer.css';

function FoodAnalyzer() {
  const [activeTab, setActiveTab] = useState('manual'); // 'manual' | 'image'

  // Food Search & Selection
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [searching, setSearching] = useState(false);

  // Meal Builder Items
  const [mealName, setMealName] = useState('Healthy Lunch Bowl');
  const [mealCategory, setMealCategory] = useState('LUNCH');
  const [mealItems, setMealItems] = useState([
    { foodName: 'Quinoa (Cooked)', portion: 1.0, unit: 'cup' },
    { foodName: 'Chickpeas (Cooked)', portion: 1.0, unit: 'cup' },
    { foodName: 'Spinach (Raw)', portion: 50, unit: 'g' }
  ]);

  // Selected item inputs for adding
  const [customItemName, setCustomItemName] = useState('');
  const [customPortion, setCustomPortion] = useState(1);
  const [customUnit, setCustomUnit] = useState('serving');

  // Analysis Result
  const [analysis, setAnalysis] = useState(null);
  const [analyzing, setAnalyzing] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState('');

  // Image Analysis State
  const [selectedFile, setSelectedFile] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [imageLoading, setImageLoading] = useState(false);
  const [visionResult, setVisionResult] = useState(null);
  const [visionError, setVisionError] = useState('');

  // Initial analysis on mount
  useEffect(() => {
    runAnalysis();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Handle Search Input
  const handleSearch = async (val) => {
    setSearchQuery(val);
    if (val.trim().length < 2) {
      setSearchResults([]);
      return;
    }

    setSearching(true);
    try {
      const results = await foodService.searchFoods(val);
      setSearchResults(results.slice(0, 8));
    } catch (err) {
      console.error(err);
    } finally {
      setSearching(false);
    }
  };

  const handleSelectFood = (food) => {
    setMealItems(prev => [
      ...prev,
      { foodName: food.name, portion: 1.0, unit: food.servingUnit || 'serving' }
    ]);
    setSearchQuery('');
    setSearchResults([]);
  };

  const handleAddCustom = (e) => {
    e.preventDefault();
    if (!customItemName.trim()) return;

    setMealItems(prev => [
      ...prev,
      { foodName: customItemName.trim(), portion: Number(customPortion) || 1.0, unit: customUnit }
    ]);
    setCustomItemName('');
    setCustomPortion(1);
  };

  const handleRemoveItem = (index) => {
    setMealItems(prev => prev.filter((_, i) => i !== index));
  };

  const handleUpdateItemPortion = (index, newPortion) => {
    setMealItems(prev => prev.map((item, i) => i === index ? { ...item, portion: Math.max(0.1, Number(newPortion) || 1.0) } : item));
  };

  const runAnalysis = async () => {
    if (mealItems.length === 0) return;

    setAnalyzing(true);
    setSaveSuccess('');
    try {
      const res = await foodService.analyzeMeal({
        mealName: mealName || 'Custom Meal',
        mealType: mealCategory,
        items: mealItems
      });
      setAnalysis(res);
    } catch (err) {
      alert('Analysis failed: ' + (err.response?.data || err.message));
    } finally {
      setAnalyzing(false);
    }
  };

  // Save to Journal
  const handleSaveToJournal = async () => {
    if (!analysis) return;
    try {
      const today = new Date().toISOString().split('T')[0];
      for (const item of analysis.itemBreakdown || []) {
        await journalService.addFoodToMeal({
          date: today,
          mealType: mealCategory,
          foodName: item.name,
          portion: item.portion,
          unit: item.unit,
          calories: item.calories,
          protein: item.protein_g,
          fat: item.fat_g,
          carbs: item.carbs_g,
          fiber: item.fiber_g,
          cost: 1.50 * item.portion
        });
      }
      setSaveSuccess(`Successfully logged "${mealName}" to today's ${mealCategory.toLowerCase()}!`);
    } catch (err) {
      alert('Error saving meal: ' + err.message);
    }
  };

  // Image Upload Handling
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedFile(file);
      setImagePreview(URL.createObjectURL(file));
      setVisionResult(null);
      setVisionError('');
    }
  };

  const handleRunImageAnalysis = async () => {
    if (!selectedFile) return;

    setImageLoading(true);
    setVisionError('');
    try {
      const res = await foodService.analyzeFoodImage(selectedFile);
      if (res.success) {
        setVisionResult(res);
      } else {
        setVisionError(res.error || 'Failed to analyze food image.');
      }
    } catch (err) {
      setVisionError(err.response?.data?.error || err.message || 'Image analysis service encountered an error.');
    } finally {
      setImageLoading(false);
    }
  };

  const handleAddCandidateToMeal = (cand) => {
    setMealItems(prev => [
      ...prev,
      { foodName: cand.name, portion: 1.0, unit: cand.defaultServing || 'serving' }
    ]);
    setActiveTab('manual');
    runAnalysis();
  };

  return (
    <div className="analyzer-page">
      {/* Page Header */}
      <div className="analyzer-header">
        <div>
          <h1 className="analyzer-title">Intelligent Food & Meal Analyzer</h1>
          <p className="analyzer-sub">
            Deconstruct meals into verified macro and micro nutrients, assess dietary balance, and explore smart food substitutions.
          </p>
        </div>

        {/* Tab Switcher */}
        <div className="analyzer-tab-switcher">
          <button
            className={`tab-btn ${activeTab === 'manual' ? 'active' : ''}`}
            onClick={() => setActiveTab('manual')}
          >
            <Layers size={17} /> Meal Builder
          </button>
          <button
            className={`tab-btn ${activeTab === 'image' ? 'active' : ''}`}
            onClick={() => setActiveTab('image')}
          >
            <Camera size={17} /> Vision Food Detection
          </button>
        </div>
      </div>

      {saveSuccess && (
        <div className="alert-banner alert-success">
          <CheckCircle size={18} /> {saveSuccess}
        </div>
      )}

      {/* VIEW: IMAGE ANALYSIS TAB */}
      {activeTab === 'image' && (
        <div className="image-analysis-section">
          <div className="card image-upload-card">
            <h3 className="section-card-title">Food Image Identification</h3>
            <p className="section-card-sub">
              Upload a clear photo of your dish. Our modular vision service identifies candidate foods from our verified database.
            </p>

            <div className="upload-dropzone">
              {imagePreview ? (
                <div className="image-preview-box">
                  <img src={imagePreview} alt="Food dish preview" className="uploaded-dish-img" />
                  <button className="change-img-btn" onClick={() => setSelectedFile(null)}>
                    Choose different photo
                  </button>
                </div>
              ) : (
                <label className="upload-label-area">
                  <UploadCloud size={48} className="upload-cloud-icon" />
                  <span className="upload-prompt-main">Click or drop food photo here</span>
                  <span className="upload-prompt-sub">Supports JPEG, PNG, or WebP up to 5MB</span>
                  <input
                    type="file"
                    accept="image/jpeg,image/png,image/webp"
                    className="file-hidden-input"
                    onChange={handleFileChange}
                  />
                </label>
              )}
            </div>

            {selectedFile && !visionResult && (
              <button
                className="btn-primary btn-block"
                onClick={handleRunImageAnalysis}
                disabled={imageLoading}
              >
                {imageLoading ? 'Identifying Food Candidates...' : 'Analyze Food Photo'}
              </button>
            )}

            {visionError && <div className="alert-banner alert-error">{visionError}</div>}
          </div>

          {/* Vision Candidate Results */}
          {visionResult && (
            <div className="card vision-results-card">
              <div className="vision-results-header">
                <div>
                  <h3 className="section-card-title">Identified Food Candidates</h3>
                  <span className="badge badge-verified">Traceable Estimates</span>
                </div>
              </div>

              <div className="confidence-notice-banner">
                <Info size={18} className="notice-icon" />
                <p>{visionResult.confidenceNotice}</p>
              </div>

              <div className="candidates-list">
                {visionResult.candidates?.map((cand, idx) => (
                  <div key={idx} className="candidate-row-card">
                    <div className="candidate-info">
                      <div className="candidate-name-row">
                        <span className="candidate-name">{cand.name}</span>
                        <span className="candidate-conf-badge">
                          {Math.round(cand.confidence * 100)}% match confidence
                        </span>
                      </div>
                      <span className="candidate-nut-sub">
                        Category: {cand.category} &bull; {Math.round(cand.caloriesPerServing)} kcal &bull; {cand.proteinG}g protein ({cand.defaultServing})
                      </span>
                    </div>
                    <button
                      className="btn-secondary btn-sm"
                      onClick={() => handleAddCandidateToMeal(cand)}
                    >
                      <Plus size={15} /> Add to Meal Builder
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {/* VIEW: MEAL BUILDER & ANALYSIS TAB */}
      {activeTab === 'manual' && (
        <div className="analyzer-builder-layout">
          {/* Left Column: Meal Builder & Ingredients List */}
          <div className="builder-controls-column">
            {/* Meal Metadata */}
            <div className="card">
              <h3 className="section-card-title">1. Meal Configuration</h3>
              <div className="meal-meta-inputs">
                <div className="form-group">
                  <label className="form-label">Meal Name</label>
                  <input
                    type="text"
                    className="form-input"
                    value={mealName}
                    onChange={(e) => setMealName(e.target.value)}
                    placeholder="e.g. Quinoa Salad Bowl"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Category</label>
                  <select
                    className="form-select"
                    value={mealCategory}
                    onChange={(e) => setMealCategory(e.target.value)}
                  >
                    <option value="BREAKFAST">Breakfast</option>
                    <option value="LUNCH">Lunch</option>
                    <option value="DINNER">Dinner</option>
                    <option value="SNACK">Snack</option>
                  </select>
                </div>
              </div>
            </div>

            {/* Food Search & Quick Add */}
            <div className="card">
              <h3 className="section-card-title">2. Add Ingredients</h3>
              <p className="section-card-sub">Search our verified food database or enter any ingredient</p>

              <div className="search-input-wrap">
                <Search size={18} className="search-icon" />
                <input
                  type="text"
                  className="search-input"
                  placeholder="Search food item (e.g. Avocado, Paneer, Salmon, Lentils)..."
                  value={searchQuery}
                  onChange={(e) => handleSearch(e.target.value)}
                />
                {searching && <span className="searching-indicator" style={{ fontSize: '0.8rem', color: 'var(--text-muted)', alignSelf: 'center', marginRight: '0.5rem' }}>Searching...</span>}
              </div>

              {/* Autocomplete Dropdown */}
              {searchResults.length > 0 && (
                <div className="search-dropdown-menu">
                  {searchResults.map((food) => (
                    <div
                      key={food.id}
                      className="search-dropdown-item"
                      onClick={() => handleSelectFood(food)}
                    >
                      <div>
                        <span className="dropdown-food-name">{food.name}</span>
                        <span className="dropdown-food-sub">
                          {food.category} &bull; {Math.round(food.calories)} kcal / {food.servingUnit}
                        </span>
                      </div>
                      <span className="badge badge-verified">{food.source}</span>
                    </div>
                  ))}
                </div>
              )}

              {/* Or Manual Custom Item Add */}
              <form onSubmit={handleAddCustom} className="manual-item-form">
                <div className="manual-inputs-row">
                  <input
                    type="text"
                    placeholder="Ingredient name"
                    className="form-input flex-2"
                    value={customItemName}
                    onChange={(e) => setCustomItemName(e.target.value)}
                  />
                  <input
                    type="number"
                    min="0.1"
                    step="0.1"
                    placeholder="Portion"
                    className="form-input flex-1"
                    value={customPortion}
                    onChange={(e) => setCustomPortion(e.target.value)}
                  />
                  <select
                    className="form-select flex-1"
                    value={customUnit}
                    onChange={(e) => setCustomUnit(e.target.value)}
                  >
                    <option value="serving">serving</option>
                    <option value="g">grams (g)</option>
                    <option value="cup">cup</option>
                    <option value="piece">piece</option>
                    <option value="tbsp">tbsp</option>
                    <option value="oz">oz</option>
                  </select>
                </div>
                <button type="submit" className="btn-secondary btn-block">
                  <Plus size={16} /> Add Custom Ingredient
                </button>
              </form>
            </div>

            {/* Meal Items Basket */}
            <div className="card">
              <div className="basket-header">
                <h3 className="section-card-title">Meal Ingredients ({mealItems.length})</h3>
                <span className="badge badge-api">Real-time Portions</span>
              </div>

              <div className="basket-items-list">
                {mealItems.map((item, index) => (
                  <div key={index} className="basket-item-row">
                    <div className="basket-item-title-col">
                      <span className="basket-item-name">{item.foodName}</span>
                      <span className="basket-item-sub">Unit: {item.unit}</span>
                    </div>
                    <div className="basket-portion-control">
                      <input
                        type="number"
                        min="0.1"
                        step="0.1"
                        className="portion-number-input"
                        value={item.portion}
                        onChange={(e) => handleUpdateItemPortion(index, e.target.value)}
                      />
                      <button
                        className="item-delete-btn"
                        onClick={() => handleRemoveItem(index)}
                        title="Remove ingredient"
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                  </div>
                ))}

                {mealItems.length === 0 && (
                  <div className="empty-basket-box">
                    <p>No ingredients in this meal yet. Search and add above.</p>
                  </div>
                )}
              </div>

              <button
                className="btn-primary btn-block mt-3"
                onClick={runAnalysis}
                disabled={analyzing || mealItems.length === 0}
              >
                {analyzing ? 'Calculating Nutrition Intelligence...' : 'Run Nutrition Intelligence'}
              </button>
            </div>
          </div>

          {/* Right Column: Complete Analysis Results */}
          <div className="analysis-results-column">
            {analysis ? (
              <div className="analysis-card-stream">
                {/* Meal Total Hero Stats */}
                <div className="card analysis-hero-card">
                  <div className="hero-top-row">
                    <div>
                      <span className="badge badge-verified">Deterministic Calculation</span>
                      <h2 className="analysis-meal-name">{analysis.mealName}</h2>
                      <span className="analysis-category-tag">{analysis.mealType}</span>
                    </div>
                    <div className="hero-calorie-badge">
                      <span className="hero-cal-num">{Math.round(analysis.calories)}</span>
                      <span className="hero-cal-label">Total kcal</span>
                    </div>
                  </div>

                  {/* Macro Progress Cards */}
                  <div className="analysis-macros-grid">
                    <div className="macro-stat-box protein-box">
                      <span className="stat-label">Protein</span>
                      <span className="stat-val">{Math.round(analysis.proteinG)}g</span>
                      <span className="stat-pct">{analysis.proteinCaloriePct}% calories</span>
                    </div>
                    <div className="macro-stat-box carbs-box">
                      <span className="stat-label">Carbohydrates</span>
                      <span className="stat-val">{Math.round(analysis.carbsG)}g</span>
                      <span className="stat-pct">{analysis.carbsCaloriePct}% calories</span>
                    </div>
                    <div className="macro-stat-box fat-box">
                      <span className="stat-label">Total Fat</span>
                      <span className="stat-val">{Math.round(analysis.fatG)}g</span>
                      <span className="stat-pct">{analysis.fatCaloriePct}% calories</span>
                    </div>
                    <div className="macro-stat-box fiber-box">
                      <span className="stat-label">Dietary Fiber</span>
                      <span className="stat-val">{analysis.fiberG}g</span>
                      <span className="stat-pct">Satiety driver</span>
                    </div>
                  </div>

                  {/* Macro Distribution Bar (AMDR) */}
                  <div className="macro-distribution-bar-wrap">
                    <span className="bar-title">Macronutrient Calorie Distribution (AMDR)</span>
                    <div className="multi-color-bar">
                      <div
                        className="bar-slice protein-slice"
                        style={{ width: `${analysis.proteinCaloriePct}%` }}
                        title={`Protein: ${analysis.proteinCaloriePct}%`}
                      />
                      <div
                        className="bar-slice carbs-slice"
                        style={{ width: `${analysis.carbsCaloriePct}%` }}
                        title={`Carbs: ${analysis.carbsCaloriePct}%`}
                      />
                      <div
                        className="bar-slice fat-slice"
                        style={{ width: `${analysis.fatCaloriePct}%` }}
                        title={`Fat: ${analysis.fatCaloriePct}%`}
                      />
                    </div>
                  </div>
                </div>

                {/* Meal Quality & Balance Score Card */}
                <div className="card balance-evaluation-card">
                  <div className="balance-eval-header">
                    <div>
                      <h3 className="section-card-title">Meal Quality & Balance Score</h3>
                      <p className="section-card-sub">Evaluated against scientific Acceptable Macronutrient Ranges</p>
                    </div>
                    <div className="score-pill-badge">
                      <span className="score-val-big">{analysis.balanceScore}</span>
                      <span className="score-val-max">/ 100</span>
                    </div>
                  </div>

                  <div className="factors-breakdown-list">
                    {Object.entries(analysis.scoreFactors || {}).map(([factor, pts]) => (
                      <div key={factor} className="factor-item">
                        <span className="factor-name">{factor}</span>
                        <span className="factor-points">+{pts} pts</span>
                      </div>
                    ))}
                  </div>

                  {/* Strengths & Potential Gaps */}
                  <div className="strengths-gaps-grid">
                    <div className="strengths-column">
                      <h5 className="sub-heading-green"><CheckCircle size={15} /> Identified Strengths</h5>
                      <ul>
                        {analysis.strengths?.map((str, i) => (
                          <li key={i}>{str}</li>
                        ))}
                      </ul>
                    </div>

                    <div className="gaps-column">
                      <h5 className="sub-heading-amber"><AlertCircle size={15} /> Potential Gaps</h5>
                      <ul>
                        {analysis.potentialGaps?.map((gap, i) => (
                          <li key={i}>{gap}</li>
                        ))}
                      </ul>
                    </div>
                  </div>
                </div>

                {/* Detailed Micronutrient Breakdown Table */}
                <div className="card">
                  <h3 className="section-card-title">Detailed Nutrient Table</h3>
                  <p className="section-card-sub">Traceable micronutrient values and daily reference alignment</p>

                  <div className="table-container">
                    <table className="nutrient-table">
                      <thead>
                        <tr>
                          <th>Nutrient</th>
                          <th>Recorded Amount</th>
                          <th>Target Share</th>
                          <th>Status</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr>
                          <td>Dietary Fiber</td>
                          <td><strong>{analysis.fiberG} g</strong></td>
                          <td>{analysis.targetPctMet?.fiber || 0}% of daily target</td>
                          <td><span className="badge badge-verified">Optimal</span></td>
                        </tr>
                        <tr>
                          <td>Sodium</td>
                          <td><strong>{analysis.sodiumMg} mg</strong></td>
                          <td>{analysis.targetPctMet?.sodium || 0}% of max reference</td>
                          <td>
                            <span className={`badge ${analysis.sodiumMg > 800 ? 'badge-warning' : 'badge-verified'}`}>
                              {analysis.sodiumMg > 800 ? 'Approaching High' : 'Controlled'}
                            </span>
                          </td>
                        </tr>
                        <tr>
                          <td>Potassium</td>
                          <td><strong>{analysis.potassiumMg} mg</strong></td>
                          <td>{Math.round((analysis.potassiumMg / 3400) * 100)}% reference</td>
                          <td><span className="badge badge-verified">Traceable</span></td>
                        </tr>
                        <tr>
                          <td>Calcium</td>
                          <td><strong>{analysis.calciumMg} mg</strong></td>
                          <td>{Math.round((analysis.calciumMg / 1000) * 100)}% reference</td>
                          <td><span className="badge badge-verified">Traceable</span></td>
                        </tr>
                        <tr>
                          <td>Iron</td>
                          <td><strong>{analysis.ironMg} mg</strong></td>
                          <td>{Math.round((analysis.ironMg / 14) * 100)}% reference</td>
                          <td><span className="badge badge-verified">Traceable</span></td>
                        </tr>
                        <tr>
                          <td>Estimated Meal Cost</td>
                          <td><strong>${analysis.estimatedCost}</strong></td>
                          <td>Budget estimate</td>
                          <td><span className="badge badge-api">Cost Per Serving</span></td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>

                {/* Smart Food Substitutions */}
                {analysis.substitutions && analysis.substitutions.length > 0 && (
                  <div className="card substitutions-card">
                    <div className="flex-between">
                      <div>
                        <h3 className="section-card-title">Smart Food Substitutions</h3>
                        <p className="section-card-sub">Nutritionally comparable alternatives with clear trade-offs</p>
                      </div>
                      <Repeat size={20} className="text-info" />
                    </div>

                    <div className="substitutions-list">
                      {analysis.substitutions.map((sub, idx) => (
                        <div key={idx} className="sub-item-box">
                          <div className="sub-swap-line">
                            <span className="sub-orig">{sub.originalFood}</span>
                            <ArrowRight size={16} className="swap-arrow" />
                            <span className="sub-replacement">{sub.substituteFood}</span>
                          </div>
                          <p className="sub-rationale"><strong>Rationale:</strong> {sub.rationale}</p>
                          <p className="sub-tradeoff"><strong>Trade-off:</strong> {sub.tradeOff}</p>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* Action Bar: Save to Journal */}
                <div className="analysis-action-bar">
                  <button className="btn-primary btn-lg" onClick={handleSaveToJournal}>
                    <CheckCircle size={18} /> Save & Log to Today's Journal
                  </button>
                </div>
              </div>
            ) : (
              <div className="card empty-analysis-placeholder">
                <Scale size={48} className="scale-icon" />
                <h3>No Meal Analyzed Yet</h3>
                <p>Add ingredients in the builder on the left and click "Run Nutrition Intelligence" to see full nutrient totals and balance scores.</p>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default FoodAnalyzer;
