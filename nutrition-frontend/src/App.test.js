import { render, screen } from '@testing-library/react';
import App from './App';

test('renders sign in page by default for unauthenticated user', () => {
  render(<App />);
  const heading = screen.getByRole('heading', { name: /sign in/i });
  expect(heading).toBeInTheDocument();
});
