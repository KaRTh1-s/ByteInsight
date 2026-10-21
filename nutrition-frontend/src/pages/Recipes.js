import React, { useState, useEffect } from 'react';
import {
  Clock,
  Flame,
  Search,
  Repeat,
  ArrowRight,
  ChevronRight
} from 'lucide-react';
import { recipeService } from '../services/api';
import './Recipes.css';

const substitutionPresets = [
  {
    orig: 'White Rice (Cooked)',
    sub: 'Quinoa (Cooked)',
    reason: 'Adds +3.6g fiber and +3.9g protein with complete amino acid profile.',
    calDiff: '+17 kcal',
    proteinDiff: '+3.9g',
    fiberDiff: '+4.6g',
    sodiumDiff: '+13mg',
    costDiff: '+$0.65'
  },
  {
    orig: 'Lean Ground Beef (80/20)',
    sub: 'Cooked Lentils (Dal)',
    reason: 'Eliminates cholesterol and saturated fat while quadrupling dietary fiber.',
    calDiff: '-110 kcal',
    proteinDiff: '-6.0g',
    fiberDiff: '+15.6g',
    sodiumDiff: '-70mg',
    costDiff: '-$1.80'
  },
  {
    orig: 'Heavy Cream / Mayonnaise',
    sub: 'Greek Yogurt (Plain Non-Fat)',
    reason: 'Drastically reduces dietary fat while adding 17g bioavailable dairy protein.',
    calDiff: '-240 kcal',
    proteinDiff: '+16.0g',
    fiberDiff: '0g',
    sodiumDiff: '-40mg',
    costDiff: '-$0.50'
  },
  {
    orig: 'Regular Pasta',
    sub: 'Lentil Pasta (Cooked)',
    reason: 'Triples protein and doubles fiber; naturally gluten-free.',
    calDiff: '-40 kcal',
    proteinDiff: '+7.8g',
    fiberDiff: '+4.4g',
    sodiumDiff: '+10mg',
    costDiff: '+$0.75'
  }
];

function Recipes() {
  const [recipes, setRecipes] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [loading, setLoading] = useState(true);
  const [activeRecipe, setActiveRecipe] = useState(null);

  // Active Substitution Tool Preset
  const [activeSub, setActiveSub] = useState(substitutionPresets[0]);

  const loadRecipes = async (query = '', category = '') => {
    try {
      setLoading(true);
      const data = await recipeService.getRecipes(query, category);
      setRecipes(data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRecipes(searchQuery, selectedCategory);
  }, [searchQuery, selectedCategory]);

  return (
    <div className="recipes-page">
      {/* Top Banner */}
      <div className="recipes-header card">
        <div>
          <span className="badge badge-verified">Chef-Crafted Nutrition</span>
          <h1 className="recipes-title">Recipes & Food Optimization</h1>
          <p className="recipes-sub">
            Nutrient-dense recipes with verified macro balances and an interactive food substitution comparison engine.
          </p>
        </div>
      </div>

      {/* Interactive Food Substitution Engine Tool */}
      <div className="card sub-tool-card">
        <div className="sub-tool-header">
          <div className="flex-center-gap">
            <Repeat size={22} className="text-info" />
            <h3 className="section-card-title">Intelligent Substitution Engine</h3>
          </div>
          <span className="badge badge-api">Evidence-Based Trade-offs</span>
        </div>

        <p className="sub-tool-intro">
          Select a common ingredient swap below to examine verified nutritional trade-offs before modifying your meals:
        </p>

        {/* Preset Selector Chips */}
        <div className="sub-presets-row">
          {substitutionPresets.map((preset, idx) => (
            <button
              key={idx}
              className={`preset-chip-btn ${activeSub === preset ? 'active' : ''}`}
              onClick={() => setActiveSub(preset)}
            >
              <span className="preset-orig">{preset.orig.split(' ')[0]}</span>
              <ArrowRight size={13} />
              <span className="preset-sub">{preset.sub.split(' ')[0]}</span>
            </button>
          ))}
        </div>

        {/* Comparison Box */}
        {activeSub && (
          <div className="sub-comparison-box">
            <div className="sub-foods-row">
              <div className="food-side-card orig-side">
                <span className="side-label">Original Ingredient</span>
                <h4 className="side-food-name">{activeSub.orig}</h4>
              </div>

              <div className="swap-icon-bubble">
                <Repeat size={20} />
              </div>

              <div className="food-side-card new-side">
                <span className="side-label">Optimized Substitute</span>
                <h4 className="side-food-name text-success">{activeSub.sub}</h4>
              </div>
            </div>

            <div className="sub-metrics-bar">
              <div className="metric-diff-chip">
                <span>Calories:</span> <strong>{activeSub.calDiff}</strong>
              </div>
              <div className="metric-diff-chip">
                <span>Protein:</span> <strong>{activeSub.proteinDiff}</strong>
              </div>
              <div className="metric-diff-chip">
                <span>Fiber:</span> <strong>{activeSub.fiberDiff}</strong>
              </div>
              <div className="metric-diff-chip">
                <span>Sodium:</span> <strong>{activeSub.sodiumDiff}</strong>
              </div>
              <div className="metric-diff-chip">
                <span>Est. Cost:</span> <strong>{activeSub.costDiff}</strong>
              </div>
            </div>

            <div className="sub-explanation-footer">
              <strong>Nutritional Rationale:</strong> {activeSub.reason}
            </div>
          </div>
        )}
      </div>

      {/* Recipe Discovery Filter & Search Bar */}
      <div className="recipes-filter-bar card">
        <div className="search-wrap">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            className="search-input"
            placeholder="Search recipes by title, cuisine, or ingredient..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        <div className="category-chips-list">
          {['', 'Breakfast', 'Lunch', 'Dinner'].map((cat) => (
            <button
              key={cat}
              className={`cat-chip-btn ${selectedCategory === cat ? 'active' : ''}`}
              onClick={() => setSelectedCategory(cat)}
            >
              {cat === '' ? 'All Recipes' : cat}
            </button>
          ))}
        </div>
      </div>

      {/* Recipes Cards Grid */}
      {loading ? (
        <div className="card text-center" style={{ padding: '3rem' }}>
          <div className="loading-spinner"></div>
          <p style={{ marginTop: '1rem', color: 'var(--text-muted)' }}>Loading recipes...</p>
        </div>
      ) : (
        <div className="recipes-cards-grid">
        {recipes.map((recipe) => (
          <div key={recipe.id} className="card recipe-display-card">
            <div className="recipe-card-top">
              <span className="recipe-cat-badge">{recipe.category}</span>
              <span className="recipe-cuisine-pill">{recipe.cuisine}</span>
            </div>

            <h3 className="recipe-card-title">{recipe.title}</h3>
            <p className="recipe-card-desc">{recipe.description}</p>

            <div className="recipe-timing-row">
              <span className="time-stat">
                <Clock size={14} /> {recipe.prepTimeMinutes + recipe.cookTimeMinutes} mins
              </span>
              <span className="time-stat">
                <Flame size={14} className="text-warning" /> {Math.round(recipe.caloriesPerServing)} kcal / serving
              </span>
            </div>

            <div className="recipe-macro-chips">
              <span className="chip-p">P: {recipe.proteinG}g</span>
              <span className="chip-c">C: {recipe.carbsG}g</span>
              <span className="chip-f">F: {recipe.fatG}g</span>
              <span className="chip-fib">Fib: {recipe.fiberG}g</span>
            </div>

            <div className="recipe-card-footer">
              <span className="recipe-cost">${recipe.estimatedCostPerServing?.toFixed(2)} / serving</span>
              <button
                className="btn-primary btn-sm"
                onClick={() => setActiveRecipe(recipe)}
              >
                View Recipe & Ingredients <ChevronRight size={15} />
              </button>
            </div>
          </div>
        ))}
        </div>
      )}

      {/* Recipe Modal Drawer */}
      {activeRecipe && (
        <div className="modal-backdrop" onClick={() => setActiveRecipe(null)}>
          <div className="modal-card recipe-detail-modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-recipe-header">
              <div>
                <span className="badge badge-verified">{activeRecipe.category} &bull; {activeRecipe.cuisine}</span>
                <h2 className="modal-recipe-title">{activeRecipe.title}</h2>
              </div>
              <button className="btn-secondary btn-sm" onClick={() => setActiveRecipe(null)}>Close</button>
            </div>

            <p className="modal-recipe-desc">{activeRecipe.description}</p>

            <div className="modal-recipe-stats-row">
              <div>
                <span className="stat-label">Calories</span>
                <span className="stat-val text-primary-color">{Math.round(activeRecipe.caloriesPerServing)} kcal</span>
              </div>
              <div>
                <span className="stat-label">Protein</span>
                <span className="stat-val">{activeRecipe.proteinG}g</span>
              </div>
              <div>
                <span className="stat-label">Dietary Fiber</span>
                <span className="stat-val">{activeRecipe.fiberG}g</span>
              </div>
              <div>
                <span className="stat-label">Sodium</span>
                <span className="stat-val">{Math.round(activeRecipe.sodiumMg)}mg</span>
              </div>
            </div>

            {/* Step-by-Step Instructions */}
            <div className="recipe-instructions-section">
              <h4 className="instructions-title">Preparation & Cooking Steps</h4>
              {(() => {
                try {
                  const steps = JSON.parse(activeRecipe.instructionsJson || '[]');
                  return (
                    <ol className="steps-ordered-list">
                      {steps.map((st, i) => (
                        <li key={i}>{st}</li>
                      ))}
                    </ol>
                  );
                } catch {
                  return <p>Follow wholesome preparation instructions and serve fresh.</p>;
                }
              })()}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Recipes;
