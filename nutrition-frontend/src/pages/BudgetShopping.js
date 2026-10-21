import React, { useState, useEffect } from 'react';
import {
  Plus,
  Trash2,
  TrendingDown,
  ShoppingBag
} from 'lucide-react';
import { shoppingListService, profileService } from '../services/api';
import './BudgetShopping.css';

function BudgetShopping() {
  const [items, setItems] = useState([]);
  const [profile, setProfile] = useState(null);
  const [newItemName, setNewItemName] = useState('');
  const [newItemQty, setNewItemQty] = useState(1);
  const [newItemUnit, setNewItemUnit] = useState('pack');
  const [newItemCost, setNewItemCost] = useState(2.50);
  const [newItemCategory, setNewItemCategory] = useState('Produce');
  const [loading, setLoading] = useState(true);

  const loadData = async () => {
    try {
      setLoading(true);
      const [listData, profData] = await Promise.all([
        shoppingListService.getList(),
        profileService.getProfile()
      ]);
      setItems(listData || []);
      setProfile(profData);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleAddItem = async (e) => {
    e.preventDefault();
    if (!newItemName.trim()) return;

    try {
      const added = await shoppingListService.addItem({
        itemName: newItemName.trim(),
        quantity: Number(newItemQty) || 1,
        unit: newItemUnit,
        estimatedCost: Number(newItemCost) || 2.0,
        category: newItemCategory
      });
      setItems(prev => [added, ...prev]);
      setNewItemName('');
      setNewItemQty(1);
      setNewItemCost(2.50);
    } catch (err) {
      alert('Error adding item: ' + err.message);
    }
  };

  const handleToggle = async (id) => {
    try {
      const updated = await shoppingListService.toggleItem(id);
      setItems(prev => prev.map(item => item.id === id ? updated : item));
    } catch (err) {
      alert('Error updating item: ' + err.message);
    }
  };

  const handleDelete = async (id) => {
    try {
      await shoppingListService.deleteItem(id);
      setItems(prev => prev.filter(item => item.id !== id));
    } catch (err) {
      alert('Error deleting item: ' + err.message);
    }
  };

  const handleClearPurchased = async () => {
    try {
      await shoppingListService.clearPurchased();
      setItems(prev => prev.filter(item => !item.isPurchased));
    } catch (err) {
      alert('Error clearing purchased items: ' + err.message);
    }
  };

  const totalEstimatedCost = items.reduce((sum, it) => sum + (it.estimatedCost || 0), 0);
  const purchasedCost = items.filter(it => it.isPurchased).reduce((sum, it) => sum + (it.estimatedCost || 0), 0);
  const weeklyBudget = (profile?.budgetPerDay || 15) * 7;

  if (loading) {
    return (
      <div className="budget-page">
        <div className="card text-center" style={{ padding: '3rem' }}>
          <div className="loading-spinner"></div>
          <p style={{ marginTop: '1rem', color: 'var(--text-muted)' }}>Loading your budget and grocery list...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="budget-page">
      {/* Header Banner */}
      <div className="budget-header card">
        <div>
          <span className="badge badge-verified">Budget Intelligence</span>
          <h1 className="budget-title">Budget-Aware Grocery & Nutrition</h1>
          <p className="budget-sub">
            Synchronize meal plans with grocery expenses, identify high-yield affordable nutrients, and manage your shopping list.
          </p>
        </div>
      </div>

      {/* Budget Summary Meter Row */}
      <div className="budget-metrics-grid">
        <div className="card budget-metric-card">
          <span className="b-tag">Total Shopping Estimate</span>
          <span className="b-val">${totalEstimatedCost.toFixed(2)}</span>
          <span className="b-sub">{items.length} items on grocery list</span>
        </div>

        <div className="card budget-metric-card">
          <span className="b-tag">Already Purchased</span>
          <span className="b-val text-success">${purchasedCost.toFixed(2)}</span>
          <span className="b-sub">{items.filter(i => i.isPurchased).length} items completed</span>
        </div>

        <div className="card budget-metric-card">
          <span className="b-tag">Weekly Configured Budget</span>
          <span className="b-val">${weeklyBudget.toFixed(2)}</span>
          <span className="b-sub">Target: ${(profile?.budgetPerDay || 15).toFixed(2)} / day</span>
        </div>
      </div>

      {/* Low-Cost Nutrient-Dense Recommendations */}
      <div className="card cost-efficiency-card">
        <div className="flex-between">
          <div className="flex-center-gap">
            <TrendingDown size={20} className="text-success" />
            <h3 className="section-card-title">High-Yield Budget Nutrition Insights</h3>
          </div>
          <span className="badge badge-verified">Cost Per Gram Protein</span>
        </div>

        <div className="efficiency-chips-grid">
          <div className="eff-chip">
            <span className="eff-food">Cooked Lentils / Dal</span>
            <span className="eff-stat">~$0.04 per gram protein</span>
            <span className="eff-desc">High in folate, iron, and fiber for under $0.80/serving.</span>
          </div>
          <div className="eff-chip">
            <span className="eff-food">Whole Pasture Eggs</span>
            <span className="eff-stat">~$0.05 per gram protein</span>
            <span className="eff-desc">Complete bioavailable amino acids + choline at ~$0.30/egg.</span>
          </div>
          <div className="eff-chip">
            <span className="eff-food">Rolled Oats</span>
            <span className="eff-stat">~$0.07 per gram protein</span>
            <span className="eff-desc">Complex beta-glucan carbs and fiber at ~$0.45/bowl.</span>
          </div>
          <div className="eff-chip">
            <span className="eff-food">Canned Tuna in Water</span>
            <span className="eff-stat">~$0.06 per gram protein</span>
            <span className="eff-desc">25g lean protein with zero saturated fat for ~$1.60.</span>
          </div>
        </div>
      </div>

      {/* Shopping List Management */}
      <div className="shopping-list-layout">
        {/* Add Item Form Column */}
        <div className="card add-grocery-card">
          <h3 className="section-card-title">Add Grocery Item</h3>
          <p className="section-card-sub">Manually add pantry staples or produce</p>

          <form onSubmit={handleAddItem} className="add-grocery-form">
            <div className="form-group">
              <label className="form-label">Item Name</label>
              <input
                type="text"
                placeholder="e.g. Spinach, Greek Yogurt, Brown Rice"
                className="form-input"
                value={newItemName}
                onChange={(e) => setNewItemName(e.target.value)}
                required
              />
            </div>

            <div className="form-row-2">
              <div className="form-group">
                <label className="form-label">Quantity</label>
                <input
                  type="number"
                  min="0.5"
                  step="0.5"
                  className="form-input"
                  value={newItemQty}
                  onChange={(e) => setNewItemQty(e.target.value)}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Unit</label>
                <select
                  className="form-select"
                  value={newItemUnit}
                  onChange={(e) => setNewItemUnit(e.target.value)}
                >
                  <option value="pack">pack</option>
                  <option value="bunch">bunch</option>
                  <option value="can">can</option>
                  <option value="tubs">tubs</option>
                  <option value="carton">carton</option>
                  <option value="kg">kg</option>
                  <option value="bottle">bottle</option>
                </select>
              </div>
            </div>

            <div className="form-row-2">
              <div className="form-group">
                <label className="form-label">Est. Cost ($)</label>
                <input
                  type="number"
                  min="0.1"
                  step="0.1"
                  className="form-input"
                  value={newItemCost}
                  onChange={(e) => setNewItemCost(e.target.value)}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Aisle Category</label>
                <select
                  className="form-select"
                  value={newItemCategory}
                  onChange={(e) => setNewItemCategory(e.target.value)}
                >
                  <option value="Produce">Produce</option>
                  <option value="Dairy & Refrigerated">Dairy & Refrigerated</option>
                  <option value="Bakery & Grains">Bakery & Grains</option>
                  <option value="Proteins">Proteins</option>
                  <option value="Pantry & Spices">Pantry & Spices</option>
                </select>
              </div>
            </div>

            <button type="submit" className="btn-primary btn-block">
              <Plus size={16} /> Add to Grocery List
            </button>
          </form>
        </div>

        {/* List Column */}
        <div className="card grocery-items-card">
          <div className="grocery-list-header">
            <div>
              <h3 className="section-card-title">My Grocery Items ({items.length})</h3>
              <p className="section-card-sub">Check off items as you shop</p>
            </div>
            {items.some(i => i.isPurchased) && (
              <button className="btn-secondary btn-sm" onClick={handleClearPurchased}>
                Clear Completed Items
              </button>
            )}
          </div>

          <div className="grocery-items-stream">
            {items.length > 0 ? (
              items.map((item) => (
                <div key={item.id} className={`grocery-row ${item.isPurchased ? 'purchased-done' : ''}`}>
                  <label className="grocery-check-label">
                    <input
                      type="checkbox"
                      checked={item.isPurchased}
                      onChange={() => handleToggle(item.id)}
                      className="custom-checkbox"
                    />
                    <div className="grocery-details">
                      <span className="grocery-name">{item.itemName}</span>
                      <span className="grocery-meta">
                        {item.quantity} {item.unit} &bull; {item.category}
                      </span>
                    </div>
                  </label>

                  <div className="grocery-right">
                    <span className="grocery-price">${(item.estimatedCost || 0).toFixed(2)}</span>
                    <button
                      className="item-delete-btn"
                      onClick={() => handleDelete(item.id)}
                      title="Remove item"
                    >
                      <Trash2 size={15} />
                    </button>
                  </div>
                </div>
              ))
            ) : (
              <div className="empty-grocery-box">
                <ShoppingBag size={42} className="empty-bag-icon" />
                <p>Your shopping list is clear. Add items manually or export from the Meal Planner.</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default BudgetShopping;
