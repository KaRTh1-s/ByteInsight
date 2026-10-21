import React, { useState, useEffect } from 'react';
import { NavLink, useNavigate, useLocation } from 'react-router-dom';
import {
  LayoutDashboard,
  Apple,
  BookOpen,
  Lightbulb,
  CalendarDays,
  UtensilsCrossed,
  ShoppingCart,
  TrendingUp,
  User,
  LogOut,
  Moon,
  Sun,
  Menu,
  X,
  Flame,
  ShieldCheck
} from 'lucide-react';
import { authService, dashboardService } from '../services/api';
import './Layout.css';

const navItems = [
  { path: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { path: '/analyzer', label: 'Food Analyzer', icon: Apple },
  { path: '/journal', label: 'Meal Journal', icon: BookOpen },
  { path: '/insights', label: 'Nutrition Insights', icon: Lightbulb },
  { path: '/planner', label: 'Meal Planner', icon: CalendarDays },
  { path: '/recipes', label: 'Recipes & Swaps', icon: UtensilsCrossed },
  { path: '/shopping-list', label: 'Budget & Grocery', icon: ShoppingCart },
  { path: '/goals', label: 'Goals & Progress', icon: TrendingUp },
  { path: '/profile', label: 'Profile & Targets', icon: User },
];

function Layout({ children }) {
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);
  const [theme, setTheme] = useState(localStorage.getItem('byteinsight_theme') || 'dark');
  const [quickSummary, setQuickSummary] = useState(null);

  const currentUser = authService.getCurrentUser() || 'User';

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('byteinsight_theme', theme);
  }, [theme]);

  // Load quick daily calories
  useEffect(() => {
    if (authService.isAuthenticated()) {
      dashboardService.getSummary()
        .then(data => setQuickSummary(data))
        .catch(() => {});
    }
  }, [location.pathname]);

  const toggleTheme = () => {
    setTheme(prev => prev === 'dark' ? 'light' : 'dark');
  };

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  return (
    <div className="layout-root">
      {/* Sidebar (Desktop & Tablet) */}
      <aside className={`layout-sidebar ${mobileOpen ? 'mobile-open' : ''}`}>
        <div className="sidebar-brand">
          <div className="brand-logo-ring">
            <Flame className="brand-icon" size={24} />
          </div>
          <div className="brand-text">
            <span className="brand-title">Byte<span className="brand-title-accent">Insight</span></span>
            <span className="brand-tagline">Nutrition Intelligence</span>
          </div>
          <button className="mobile-close-btn" onClick={() => setMobileOpen(false)}>
            <X size={20} />
          </button>
        </div>

        <nav className="sidebar-nav">
          <div className="nav-group-label">INTELLIGENCE PLATFORM</div>
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
                onClick={() => setMobileOpen(false)}
              >
                <Icon size={19} className="nav-link-icon" />
                <span className="nav-link-text">{item.label}</span>
              </NavLink>
            );
          })}
        </nav>

        {/* User Card in Sidebar Footer */}
        <div className="sidebar-footer">
          <div className="user-profile-badge">
            <div className="user-avatar-circle">
              {currentUser.charAt(0).toUpperCase()}
            </div>
            <div className="user-details-column">
              <span className="user-name-text">{currentUser}</span>
              <span className="user-verified-status">
                <ShieldCheck size={12} className="verified-icon" /> Personal Profile
              </span>
            </div>
            <button
              onClick={handleLogout}
              className="logout-action-btn"
              title="Sign Out"
            >
              <LogOut size={16} />
            </button>
          </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <div className="layout-main-wrapper">
        {/* Top Header */}
        <header className="layout-top-header">
          <div className="top-header-left">
            <button
              className="mobile-hamburger-btn"
              onClick={() => setMobileOpen(true)}
              aria-label="Open Navigation Menu"
            >
              <Menu size={22} />
            </button>
            <div className="header-greeting">
              <h2 className="current-route-title">
                {navItems.find(n => n.path === location.pathname)?.label || 'Nutrition Intelligence'}
              </h2>
            </div>
          </div>

          <div className="top-header-right">
            {/* Quick Calorie Status Pill */}
            {quickSummary && (
              <div className="quick-calorie-pill" title="Today's Recorded Calories">
                <Flame size={15} className="calorie-flame-icon" />
                <span className="calorie-count-val">{Math.round(quickSummary.totalCalories || 0)}</span>
                <span className="calorie-target-divider">/ {Math.round(quickSummary.targetCalories || 2000)} kcal</span>
              </div>
            )}

            {/* Dark / Light Theme Toggle */}
            <button
              className="theme-switch-btn"
              onClick={toggleTheme}
              title={`Switch to ${theme === 'dark' ? 'Light' : 'Dark'} Mode`}
              aria-label="Toggle visual theme"
            >
              {theme === 'dark' ? <Sun size={18} /> : <Moon size={18} />}
            </button>

            <button
              onClick={handleLogout}
              className="header-logout-btn"
            >
              <LogOut size={15} />
              <span className="logout-label">Sign Out</span>
            </button>
          </div>
        </header>

        {/* Page Content View */}
        <main className="layout-content-container">
          {children}
        </main>
      </div>

      {/* Backdrop for Mobile Sidebar */}
      {mobileOpen && (
        <div className="mobile-backdrop" onClick={() => setMobileOpen(false)} />
      )}
    </div>
  );
}

export default Layout;
