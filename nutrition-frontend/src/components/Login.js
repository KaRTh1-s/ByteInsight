import React, { useState } from "react";
import axios from "axios";
import { Link , useNavigate} from "react-router-dom";
import "./Login.css";

function Login() {
  const [username, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    if (!username || !password) {
      setMessage("All fields are required.");
      return;
    }

    if (username.length < 3) {
      setMessage("Username must be at least 3 characters.");
      return;
    }

    if (password.length < 6) {
      setMessage("Password must be at least 6 characters.");
      return;
    }

    try {
      const res = await axios.post("http://localhost:8080/api/auth/login", {username,password,});
      setMessage(res.data);
      localStorage.setItem("loggedInUser", username);
      navigate("/Nutrition");
    } catch (err) {
      if (err.response) 
        setMessage(err.response.data);
      else 
        setMessage("Login failed. Server not reachable.");
    }
  };

  return (
    <div className="login-body">
      <div className="login-content">
        <h2 className="login-h">Login</h2>
        <form onSubmit={handleLogin} className="login-form">
          <input type="text" placeholder="Username" value={username} onChange={(e) => setUserName(e.target.value)}/><br />
          <input type="password" placeholder="Password" value={password}onChange={(e) => setPassword(e.target.value)}/><br />
          <button type="submit" className="login-btn">Login</button>
        </form>
        {message && <p className="message">{message}</p>}
        <p>New user? <Link to="/register"  className="to-reg">Register here</Link></p>
      </div>
    </div>
  );
}

export default Login;
