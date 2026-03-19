import "./App.css";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import "highlight.js/styles/monokai.css";
import Chat from "./page/Chat";

const App = () => (
  <Router>
    <Routes>
      <Route path="/" element={<Navigate to="/chat" replace />} />
      <Route path="/chat" element={<Chat />} />
    </Routes>
  </Router>
);

export default App;
