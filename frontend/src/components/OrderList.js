import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchAllOrders } from "../services/orderService";

function OrderList() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const navigate = useNavigate();

    useEffect(() => {
        loadOrders();
    }, []);

    const loadOrders = async () => {
        try {
            const response = await fetchAllOrders();
            setOrders(response.data);
        } catch (err) {
            console.error(err);
            setError("Failed to load orders");
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <p>Loading orders...</p>;
    if (error) return <p style={{ color: "red" }}>{error}</p>;

    return (
        <div>
            <h2>Orders</h2>

            {orders.length === 0 ? (
                <p>No orders found</p>
            ) : (
                <table border="1" cellPadding="8">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Type</th>
                            <th>Kind</th>
                            <th>Quantity</th>
                            <th>Price</th>
                            <th>Status</th>
                            <th>Created At</th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map((order) => (
                            <tr
                                key={order.orderId}
                                style={{ cursor: "pointer" }}
                                onClick={() =>
                                    navigate(`/orders/${order.orderId}`)
                                }
                            >
                                <td>{order.orderId}</td>
                                <td
                                    style={{
                                        color:
                                            order.orderType === "BUY"
                                                ? "green"
                                                : "red"
                                    }}
                                >
                                    {order.orderType}
                                </td>
                                <td>{order.orderKind}</td>
                                <td>{order.quantity}</td>
                                <td>
                                    {order.orderKind === "MARKET"
                                        ? "-"
                                        : order.price}
                                </td>
                                <td>{order.orderStatus}</td>
                                <td>{order.createdAt}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default OrderList;
