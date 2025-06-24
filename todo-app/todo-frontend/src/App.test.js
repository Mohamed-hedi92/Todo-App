import { render, screen } from '@testing-library/react';
import App from './App';

test('renders the todo list container', () => {
  render(<App />);
  const todoContainer = screen.getByText(/Meine Todos/i);
  expect(todoContainer).toBeInTheDocument();
});
