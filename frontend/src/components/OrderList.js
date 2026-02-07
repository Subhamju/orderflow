import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchAllOrders } from "../services/orderService";

function OrderList() {
    const [orders, setOrders] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const navigate = useNavigate();

    useEffect(() => {
        loadOrders();
    }, [page]);

    const loadOrders = async () => {
        setLoading(true);
        try {
            const PAGE_SIZE = 10;
            const response = await fetchAllOrders(page, PAGE_SIZE);
            setOrders(response.data.content);
            setTotalPages(response.data.totalPages);
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
                <>
                    <table border="1" cellPadding="8">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Type</th>
                                <th>Kind</th>
                                <th>Qty</th>
                                <th>Price</th>
                                <th>Status</th>
                                <th>Created At</th>
                            </tr>
                        </thead>
                        <tbody>
                            {orders.map(order => (
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
                                    <td>{new Date(order.createdAt).toLocaleString()}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    <div style={{ marginTop: "12px" }}>
                        <button
                            onClick={() => setPage(p => p - 1)}
                            disabled={page === 0}
                        >
                            Prev
                        </button>

                        <span style={{ margin: "0 10px" }}>
                            Page {page + 1} of {totalPages}
                        </span>

                        <button
                            onClick={() => setPage(p => p + 1)}
                            disabled={page === totalPages - 1}
                        >
                            Next
                        </button>
                    </div>
                </>
            )}
        </div>
    );

}

export default OrderList;
