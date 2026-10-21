import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Flame, ArrowRight, Lock, User as UserIcon, Mail } from "lucide-react";
import { authService } from "../services/api";
import "./Register.css";

function Register() {
  const [username, setUserName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [strength, setStrength] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const checkStrength = (value) => {
    if (!value) {
      setStrength("");
      return;
    }

    const strongRegex =
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

    const mediumRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,}$/;

    if (strongRegex.test(value)) {
      setStrength("Strong");
    } else if (mediumRegex.test(value)) {
      setStrength("Medium");
    } else {
      setStrength("Weak");
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();

    if (!username.trim() || !password || !email.trim()) {
      setMessage("All fields are required.");
      return;
    }

    if (username.trim().length < 3) {
      setMessage("Username must be at least 3 characters.");
      return;
    }

    if (password.length < 6) {
      setMessage("Password must be at least 6 characters.");
      return;
    }

    setLoading(true);
    setMessage("");

    try {
      await authService.register(username.trim(), email.trim(), password);
      navigate("/dashboard");
    } catch (err) {
      if (err.response && err.response.data) {
        setMessage(typeof err.response.data === "string" ? err.response.data : "Registration error.");
      } else {
        setMessage("Unable to register user. Ensure backend server is reachable.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="reg-root">
      <div className="reg-panel card">
        <div className="auth-brand-badge">
          <div className="brand-logo-ring">
            <Flame size={24} className="brand-icon" />
          </div>
          <h1 className="auth-brand-name">
            Byte<span className="brand-title-accent">Insight</span>
          </h1>
          <p className="auth-tagline">Personalized Nutrition Intelligence Platform</p>
        </div>

        <h2 className="reg-title">Create Account</h2>
        <p className="reg-subtitle">Start personalizing your nutrition and tracking dietary balance</p>

        {message && (
          <div className="alert-banner alert-error mb-3">
            {message}
          </div>
        )}

        <form onSubmit={handleRegister} className="auth-form">
          <div className="form-group">
            <label className="form-label">Username</label>
            <div className="input-with-icon">
              <UserIcon size={17} className="input-prefix-icon" />
              <input
                type="text"
                placeholder="Choose a username"
                className="form-input"
                value={username}
                onChange={(e) => setUserName(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Email Address</label>
            <div className="input-with-icon">
              <Mail size={17} className="input-prefix-icon" />
              <input
                type="email"
                placeholder="name@example.com"
                className="form-input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
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
                placeholder="Create secure password"
                className="form-input"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value);
                  checkStrength(e.target.value);
                }}
                required
              />
            </div>
            {strength && (
              <div className="strength-meter-row">
                <span className="strength-label">Password strength:</span>
                <span className={`strength-badge strength-${strength.toLowerCase()}`}>
                  {strength}
                </span>
              </div>
            )}
          </div>

          <button type="submit" className="btn-primary btn-block" disabled={loading}>
            {loading ? "Creating Account..." : "Register & Get Started"} <ArrowRight size={16} />
          </button>
        </form>

        <div className="auth-footer-text">
          <span>Already have an account? </span>
          <Link to="/login" className="auth-action-link">Sign In</Link>
        </div>
      </div>
    </div>
  );
}

export default Register;
