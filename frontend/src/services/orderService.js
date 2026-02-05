import axios from "axios";

const API_URL = "http://localhost:8080/api/v1/orders"

export const placeOrder = (order, idempotencyKey) => {
    return axios.post(API_URL, order, {
        headers: {
            "Idempotency-Key": idempotencyKey
        }
    });
};

export const fetchOrderById = (id) => {
    return axios.get(`${API_URL}/${id}`);
}