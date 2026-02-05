import { useState } from "react";
import { placeOrder } from "../services/orderService";
import { v4 as uuidv4 } from "uuid";
import { useNavigate } from "react-router-dom";



function OrderForm() {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        userId: "",
        instrumentId: "",
        orderType: "BUY",
        orderKind: "LIMIT",
        quantity: "",
        price: ""
    });

    const [submittedOrder, setSubmittedOrder] = useState(null);

    const [errors, setErrors] = useState({});
    const [idempotencyKey, setIdempotencyKey] = useState(uuidv4())
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;

        if (name === "orderKind" && value === "MARKET") {
            setForm({
                ...form,
                orderKind: value,
                price: ""
            });
            return;
        }

        setForm({
            ...form,
            [name]: value
        });
    };

    const validate = () => {
        const newErrors = {};

        if (!form.userId) {
            newErrors.userId = "User ID is required";
        }

        if (!form.instrumentId) {
            newErrors.instrumentId = "Instrument ID is required";
        }

        if (!form.quantity || form.quantity <= 0) {
            newErrors.quantity = "Quantity must be greater than 0";
        }

        if (form.orderKind === "LIMIT") {
            if (!form.price || form.price <= 0) {
                newErrors.price = "Price is required for LIMIT orders";
            }
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    }

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validate()) return;

        setLoading(true);

        try {
            const response = await placeOrder(form, idempotencyKey);
            const orderId = response.data.orderId;

            // redirect to order details page
            navigate(`/orders/${orderId}`);

        } catch (error) {
            console.error(error);
            alert("Failed to place order");

        } finally {
            setLoading(false);
        }


    };

    return (
        <div>
            <h2>Place Order</h2>

            <form onSubmit={handleSubmit}>
                <div>
                    <label>User ID:</label>
                    <input
                        type="number"
                        name="userId"
                        value={form.userId}
                        onChange={handleChange}
                    />
                    {errors.userId && <small style={{ color: "red" }}>{errors.userId}</small>}
                </div>

                <div>
                    <label>Instrument ID:</label>
                    <input
                        type="number"
                        name="instrumentId"
                        value={form.instrumentId}
                        onChange={handleChange}
                    />
                    {errors.instrumentId && <small style={{ color: "red" }}>{errors.instrumentId}</small>}
                </div>

                <div>
                    <label>Order Type:</label>
                    <select
                        name="orderType"
                        value={form.orderType}
                        onChange={handleChange}
                    >
                        <option value="BUY">BUY</option>
                        <option value="SELL">SELL</option>
                    </select>
                </div>

                <div>
                    <label>Order Kind:</label>
                    <select
                        name="orderKind"
                        value={form.orderKind}
                        onChange={handleChange}
                    >
                        <option value="LIMIT">LIMIT</option>
                        <option value="MARKET">MARKET</option>
                    </select>
                </div>

                <div>
                    <label>Quantity:</label>
                    <input
                        type="number"
                        name="quantity"
                        value={form.quantity}
                        onChange={handleChange}
                    />
                    {errors.quantity && <small style={{ color: "red" }}>{errors.quantity}</small>}
                </div>

                <div>
                    <label>Price:</label>
                    <input
                        type="number"
                        name="price"
                        value={form.price}
                        onChange={handleChange}
                        disabled={form.orderKind === "MARKET"}
                    />
                    {errors.price && <small style={{ color: "red" }}>{errors.price}</small>}
                </div>

                <button type="submit" disabled={loading}>
                    {loading ? "Placing Order..." : "Submit Order"}
                </button>
            </form>


            {submittedOrder && (
                <div style={{ marginTop: "20px" }}>
                    <h3>Order Submitted</h3>
                    <pre>{JSON.stringify(submittedOrder, null, 2)}</pre>
                </div>
            )}
        </div>
    );
}


export default OrderForm;