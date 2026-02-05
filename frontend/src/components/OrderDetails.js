import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchOrderById } from "../services/orderService";


function OrderDetails() {
    const { id } = useParams();

    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        setLoading(true);
        setError("");
        loadOrder();
    }, [id]);

    const loadOrder = async () => {
        try {
            setError("");
            const response = await fetchOrderById(id);
            setOrder(response.data);
        } catch (err) {
            setError("Order not found");
        } finally {
            setLoading(false)
        }
    };

    if (loading) return <p>Loading order...</p>;
    if (error) return <p style={{ color: "red" }}>{error}</p>
    if (!order) return null;

    return (
        <div>
            <h2>Order Details</h2>

            <table border="1" cellPadding="8">
                <tbody>
                    <tr>
                        <th>Order ID</th>
                        <td>{order.orderId}</td>
                    </tr>
                    <tr>
                        <th>Order Type</th>
                        <td style={{ color: order.orderType === "BUY" ? "green" : "red" }}>
                            {order.orderType}
                        </td>
                    </tr>
                    <tr>
                        <th>Order Kind</th>
                        <td>{order.orderKind}</td>
                    </tr>
                    <tr>
                        <th>Quantity</th>
                        <td>{order.quantity}</td>
                    </tr>
                    <tr>
                        <th>Price</th>
                        <td>
                            {order.orderKind === "MARKET" ? "-" : order.price}
                        </td>
                    </tr>
                    <tr>
                        <th>Status</th>
                        <td>
                            <span
                                style={{
                                    padding: "4px 8px",
                                    borderRadius: "4px",
                                    backgroundColor:
                                        order.status === "NEW"
                                            ? "#cce5ff"
                                            : order.status === "FILLED"
                                                ? "#d4edda"
                                                : "#e2e3e5"
                                }}
                            >
                                {order.orderStatus}
                            </span>
                        </td>
                    </tr>
                    <tr>
                        <th>Created At</th>
                        <td>{order.createdAt}</td>
                    </tr>
                </tbody>
            </table>
            <button onClick={() => window.history.back()}>
                ← Back
            </button>
        </div>
    );

}

export default OrderDetails;