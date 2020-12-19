import { Button, Form, Input, message, Modal } from 'antd';
import React from 'react';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { login } from '../utils';

class Login extends React.Component {
    state = {displayModal: false}

    handelCancel = () => {
        this.setState({
            displayModal: false,
        })
    }

    signOnClick = () => {
        this.setState({
            displayModal: true,
        })
    }

    handleFinish = (data) => {
        login(data).then((data) => {
            this.setState({
                displayModal: false,
            })

            message.success(`${data.name}, welcome back to Jupiter!`);
            this.props.onSuccess();
        }).catch((e) => {
            message.error(e.message);
        })
    }

    render = () => {
        return (
            <>
                <Button shape="round" style={{ marginRight: '20px' }} onClick={this.signOnClick}>Login</Button>
                <Modal 
                    title="Log in" visible={this.state.displayModal} 
                    onCancel={this.handelCancel} footer={null} destroyOnClose={true}
                >
                    <Form name="normal_login" onFinish={this.handleFinish} preserve={false}>
                        <Form.Item name="user_id" rules={[{required: true, message: 'Username is required!'}]}>
                            <Input prefix={<UserOutlined/>} placeholder="username"/>
                        </Form.Item>

                        <Form.Item name="password" rules={[{required: true, message: 'Password is required!'}]}>
                            <Input.Password prefix={<LockOutlined/>} placeholder="password"/>
                        </Form.Item> 

                        <Form.Item>
                            <Button type="primary" htmlType="submit">Login</Button>
                        </Form.Item>                       
                    </Form>
                </Modal>
            </>
        )
    }
}

export default Login;