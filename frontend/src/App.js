import { BrowserRouter, Routes, Route } from "react-router-dom";
import OrderForm from "./components/OrderForm";
import OrderDetails from "./components/OrderDetails";
import OrderList from "./components/OrderList";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<OrderForm />} />
        <Route path="/orders" element={<OrderList />} />
        <Route path="/orders/:id" element={<OrderDetails />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
