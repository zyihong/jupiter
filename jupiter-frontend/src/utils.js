const SERVER_ORIGIN = '';

const loginUrl = `${SERVER_ORIGIN}/login`;
export const login = (credential) => {
    return fetch(loginUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(credential)
    }).then((response) => {
        if (response.status !== 200) {
            throw Error('Fail to login!');
        }

        return response.json();
    })
}