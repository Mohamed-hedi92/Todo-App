import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';
import '@testing-library/jest-dom';


test('renders the todo list container', () => {
  render(<App />);
  const todoContainer = screen.getByText(/Meine Todos/i);
  expect(todoContainer).toBeInTheDocument();
});
