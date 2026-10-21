import React, { useState } from "react";
import { Link } from "react-router-dom";
import { Layers, ArrowRight } from "lucide-react";
import { foodService } from "../services/api";
import "./Nutrition.css";

function Nutrition() {
  const [query, setQuery] = useState("");
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchNutrition = async () => {
    if (!query.trim()) {
      setError("Please enter a food item...");
      return;
    }

    setError("");
    setData([]);
    setLoading(true);

    try {
      const result = await foodService.queryNutrition(query);

      if (Array.isArray(result) && result.length > 0) {
        setData(result);
      } else if (typeof result === "string") {
        setError(result);
      } else {
        setError("No data found for this food item");
      }
    } catch (err) {
      setError(err.message || "Something went wrong...");
    }

    setLoading(false);
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter") fetchNutrition();
  };

  return (
    <div className="nut-body">
      <div className="nut-header-wrap">
        <h1 className="nut-title">ByteInsight Food Lookup</h1>
        <p className="nut-sub">Quick single-item nutrient query. For multi-ingredient meals and balance scores, use Food Analyzer.</p>
        <Link to="/analyzer" className="btn-primary btn-sm mt-2">
          <Layers size={16} /> Open Complete Food Analyzer <ArrowRight size={15} />
        </Link>
      </div>

      <div className="nut-input-card card">
        <div className="nut-input">
          <input
            type="text"
            placeholder="Enter food item(s) e.g. Apple, Salmon, 100g Chicken..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyPress={handleKeyPress}
          />
          <button className="btn-primary" onClick={fetchNutrition}>Search</button>
        </div>
      </div>

      {loading && <p className="nut-load">Querying nutrition data...</p>}
      {error && <p className="nut-error">{error}</p>}

      {data.length > 0 && (
        <div className="nut-result">
          {data.map((item, index) => (
            <div className="card nut-card" key={index}>
              <div className="nut-card-header">
                <h2>{item.name}</h2>
                <span className="badge badge-verified">{item.source || "CALORIE_NINJAS"}</span>
              </div>
              <p className="nut-cal-row"><strong>Calories:</strong> {item.calories} kcal</p>
              <div className="nut-macros-grid">
                <div className="nut-pill"><span className="p-tag">Protein:</span> <strong>{item.protein_g} g</strong></div>
                <div className="nut-pill"><span className="f-tag">Total Fat:</span> <strong>{item.fat_total_g} g</strong></div>
                <div className="nut-pill"><span className="c-tag">Carbs:</span> <strong>{item.carbohydrates_total_g} g</strong></div>
                <div className="nut-pill"><span className="fib-tag">Fiber:</span> <strong>{item.fiber_g} g</strong></div>
              </div>
              <div className="nut-micros-list">
                <p><strong>Sugar:</strong> {item.sugar_g} g</p>
                <p><strong>Sodium:</strong> {item.sodium_mg} mg</p>
                <p><strong>Potassium:</strong> {item.potassium_mg} mg</p>
                <p><strong>Cholesterol:</strong> {item.cholesterol_mg} mg</p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Nutrition;