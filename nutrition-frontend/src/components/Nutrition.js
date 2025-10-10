import React, { useState } from "react";
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
      const res = await fetch("http://localhost:8080/api/nutrition", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ query }),
      });

      if (!res.ok) throw new Error("Failed to fetch data from backend");

      const result = await res.json();

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
        <h1 className="nut-title">BiteInsight</h1>

        <div className="nut-input">
          <input
            type="text"
            placeholder="Enter food item(s)"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyPress={handleKeyPress}
          />
          <button onClick={fetchNutrition}>Search</button>
        </div>

        {loading && <p className="nut-load">Loading...</p>}
        {error && <p className="nut-error">{error}</p>}

        {data.length > 0 && (
          <div className="nut-result">
            {data.map((item, index) => (
              <div className="nut-card" key={index}>
              <h2>{item.name}</h2>
              <p><strong>Calories:</strong> {item.calories} kcal</p>
              <p className={item.protein_g < 5 ? "high" : ""}><strong>Protein:</strong> {item.protein_g} g</p>
              <p className={item.fat_total_g > 10 ? "high" : ""}><strong>Fat:</strong> {item.fat_total_g} g</p>
              <p className={item.carbohydrates_total_g > 50 ? "high" : ""}><strong>Carbohydrates:</strong> {item.carbohydrates_total_g} g</p>
              <p className={item.sugar_g > 20 ? "high" : ""}><strong>Sugar:</strong> {item.sugar_g} g</p>
              <p><strong>Cholesterol:</strong> {item.cholesterol_mg} mg</p>
              <p><strong>Sodium:</strong> {item.sodium_mg} mg</p>
              <p><strong>Potassium:</strong> {item.potassium_mg} mg</p>
              <p className={item.fiber_g < 3 ? "low" : ""}><strong>Fiber:</strong> {item.fiber_g} g</p>
          </div>
            ))}
          </div>
        )}
      </div>
  );
}

export default Nutrition;