import { Button, Form, Input, message, Modal } from 'antd';
import React from 'react';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { register } from '../utils';

class Register extends React.Component {
    state = {
        displayModal: false
    }

    handelCancel = () => {
        this.setState({
            displayModal: false,
        })
    }

    signUpOnClick = () => {
        this.setState({
            displayModal: true,
        })
    }

    handleFinish = (data) => {
        register(data).then(() => {
            this.setState({
                displayModal: false,
            })

            message.success(`Successfully signed up!`)
        }).catch((e) => {
            message.error(e.message);
        })
    }

    render = () => {
        return (
            <>
                <Button shape="round" type="primary" onClick={this.signUpOnClick}>Register</Button>
                <Modal 
                    title="Register" visible={this.state.displayModal} 
                    onCancel={this.handelCancel} footer={null} destroyOnClose={true}
                >
                    <Form name="normal_register" initialValues={{remember: true}} onFinish={this.handleFinish} preserve={false}>
                        <Form.Item name="user_id" rules={[{required: true, message: 'Username is required!'}]}>
                            <Input prefix={<UserOutlined/>} placeholder="username"/>
                        </Form.Item>

                        <Form.Item name="password" rules={[{required: true, message: 'Password is required!'}]}>
                            <Input prefix={<LockOutlined/>} placeholder="password"/>
                        </Form.Item> 

                        <Form.Item name="first_name" rules={[{required: true, message: 'Firstname is required!'}]}>
                            <Input placeholder="firstname"/>
                        </Form.Item> 

                        <Form.Item name="last_name" rules={[{required: true, message: 'Lastname is required!'}]}>
                            <Input placeholder="lastname"/>
                        </Form.Item> 

                        <Form.Item>
                            <Button type="primary" htmlType="submit">Register</Button>
                        </Form.Item>                       
                    </Form>
                </Modal>
            </>
        )
    }
}

export default Register;