import React from 'react';
import {Layout, Button, message} from 'antd';
import Login from './components/Login';
import {logout} from './utils';
 
const {Header, Content, Sider} = Layout;
 
class App extends React.Component {
  state = {
    loggedIn: false
  }

  signoutOnClick = () => {
    logout().then(() => {
      this.setState({
        loggedIn: false
      })

      message.success(`Successfully signed out!`);
    }).catch((e) => {
      message.error(e.message);
    })
  }

  signinOnSuccess = () => {
    this.setState({
      loggedIn: true
    })
  }

  render = () => (
    <Layout>
      <Header>
        {
          this.state.loggedIn ? 
          <Button shape="round" onClick={this.signoutOnClick}>Logout</Button> :
          (
            <>
              <Login onSuccess={this.signinOnSuccess} />
            </>
          )
        }
      </Header>
      <Layout>
        <Sider width={300} className="site-layout-background">
          {'Sider'}
        </Sider>
        <Layout style={{ padding: '24px' }}>
          <Content
            className="site-layout-background"
            style={{
              padding: 24,
              margin: 0,
              height: 800,
              overflow: 'auto'
            }}
          >
            {'Home'} 
          </Content>
        </Layout>
      </Layout>
    </Layout>
  )
}
 
export default App;

