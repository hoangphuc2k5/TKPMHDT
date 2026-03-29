async function api(method, url, body) {
    const options = {
        method: method,
        headers: { "Content-Type": "application/json" }
    };
    if (body !== undefined && body !== null) {
        options.body = JSON.stringify(body);
    }
    const response = await fetch(url, options);
    const text = await response.text();
    let data = text;
    try {
        data = text ? JSON.parse(text) : {};
    } catch (e) {
        // keep raw text
    }
    if (!response.ok) {
        throw new Error(typeof data === "string" ? data : JSON.stringify(data));
    }
    return data;
}

async function apiUpload(url, formData) {
    const response = await fetch(url, {
        method: "POST",
        body: formData
    });
    const text = await response.text();
    let data = text;
    try {
        data = text ? JSON.parse(text) : {};
    } catch (e) {
        // keep raw text
    }
    if (!response.ok) {
        throw new Error(typeof data === "string" ? data : JSON.stringify(data));
    }
    return data;
}

function isUUID(str) {
    const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
    return uuidRegex.test(str);
}

function showResult(elementId, data) {
    const el = document.getElementById(elementId);
    el.textContent = typeof data === "string" ? data : JSON.stringify(data, null, 2);
}

