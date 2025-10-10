import './App.css';
import {BrowserRouter as Router,Routes,Route,Navigate} from "react-router-dom";
import Register from './components/Register';
import Login from './components/Login';
import Nutrition from './components/Nutrition';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/register"/>}/>
        <Route path="/register" element={<Register/>}/>
        <Route path="/login" element={<Login/>}/>
        <Route path="/nutrition" element={<Nutrition/>}/>
      </Routes>
    </Router>
  );
}

export default App;
