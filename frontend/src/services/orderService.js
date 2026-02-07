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

export const fetchAllOrders = (page = 0, size = 10, sortField = "createdAt", sortDir = "desc") => {
    return axios.get(`${API_URL}?page=${page}&size=${size}&sort=${sortField},${sortDir}`);
};

export const cancelOrder = (orderId) => {
    return axios.post(`${API_URL}/${orderId}/cancel`);
};
