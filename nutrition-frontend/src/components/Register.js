import React, { useState } from "react";
import axios from "axios";
import { Link , useNavigate} from "react-router-dom";
import "./Register.css";

function Register() {
  const [username, setUserName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [strength, setStrength] = useState("");

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
    if (!username || !password || !email) {
      setMessage("All fields are required.");
      return;
    }
    try {
      const res = await axios.post("http://localhost:8080/api/auth/register", {
        username,
        email,
        password,
      });
      setMessage(res.data);
      navigate("/Nutrition");
    } catch (err) {
      if (err.response) setMessage(err.response.data);
      else setMessage("Error registering user.");
    }
  };

  return (
    <div className="reg-body">
      <div className="reg-content">
        <h2 className="reg-h">Register</h2>
        <form onSubmit={handleRegister} className="reg-form">
          <input type="text" placeholder="Username" value={username} onChange={(e) => setUserName(e.target.value)}/> <br />
          <input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)}/><br />
          <input type="password" placeholder="Password" value={password} onChange={(e) => {setPassword(e.target.value);checkStrength(e.target.value);}}/><br />
          {strength && (
            <p style={{color:strength === "Strong"? "green": strength === "Medium"? "orange": "red",}}>
                Password strength: {strength}
            </p>
          )}
          <button type="submit" className="reg-btn">Register</button>
        </form>
        <p className="reg-mes">{message}</p>
        <p>Already a user? <Link to="/login"  className="to-log">Login here</Link></p>
      </div>
    </div>
  );
}

export default Register;
