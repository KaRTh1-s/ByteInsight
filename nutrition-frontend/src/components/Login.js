import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Flame, ArrowRight, Lock, User as UserIcon } from "lucide-react";
import { authService } from "../services/api";
import "./Login.css";

function Login() {
  const [username, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    if (!username.trim() || !password) {
      setMessage("Username and password are required.");
      return;
    }

    if (username.length < 3) {
      setMessage("Username must be at least 3 characters.");
      return;
    }

    setLoading(true);
    setMessage("");

    try {
      await authService.login(username.trim(), password);
      navigate("/dashboard");
    } catch (err) {
      if (err.response && err.response.data) {
        setMessage(typeof err.response.data === "string" ? err.response.data : "Invalid credentials.");
      } else {
        setMessage("Unable to connect to ByteInsight server. Ensure backend is running.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-root">
      <div className="login-panel card">
        <div className="auth-brand-badge">
          <div className="brand-logo-ring">
            <Flame size={24} className="brand-icon" />
          </div>
          <h1 className="auth-brand-name">
            Byte<span className="brand-title-accent">Insight</span>
          </h1>
          <p className="auth-tagline">Personalized Nutrition Intelligence Platform</p>
        </div>

        <h2 className="login-title">Sign In</h2>
        <p className="login-subtitle">Access your personalized nutrient targets and meal journal</p>

        {message && (
          <div className="alert-banner alert-error mb-3">
            {message}
          </div>
        )}

        <form onSubmit={handleLogin} className="auth-form">
          <div className="form-group">
            <label className="form-label">Username</label>
            <div className="input-with-icon">
              <UserIcon size={17} className="input-prefix-icon" />
              <input
                type="text"
                className="form-input"
                placeholder="Enter your username"
                value={username}
                onChange={(e) => setUserName(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <div className="input-with-icon">
              <Lock size={17} className="input-prefix-icon" />
              <input
                type="password"
                className="form-input"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
          </div>

          <button type="submit" className="btn-primary btn-block" disabled={loading}>
            {loading ? "Authenticating..." : "Sign In to ByteInsight"} <ArrowRight size={16} />
          </button>
        </form>

        <div className="auth-footer-text">
          <span>New to ByteInsight? </span>
          <Link to="/register" className="auth-action-link">Create Account</Link>
        </div>
      </div>
    </div>
  );
}

export default Login;
