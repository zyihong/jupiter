import React from 'react';
import {Layout, Button, message, Menu, Col, Row} from 'antd';
import Login from './components/Login';
import Register from './components/Register';
import {logout, getTopGames} from './utils';
import CustomSearch from './components/CustomSearch';
import SubMenu from 'antd/lib/menu/SubMenu';
import Favorites from './components/Favorites';
import { LikeOutlined, FireOutlined } from '@ant-design/icons';
 
const {Header, Content, Sider} = Layout;
 
class App extends React.Component {
  state = {
    loggedIn: false,
    topGames: [],
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

  componentDidMount = () => {
    // console.log("fetch top games")
    getTopGames().then((data) => {
      this.setState({
        topGames: data
      })
    }).catch((e) => {
      message.error(e.message);
    })
  }

  render = () => (
    <Layout>
      <Header>
        <Row justify="space-between">
          <Col>
            {
              this.state.loggedIn && <Favorites />
            }
          </Col>

          <Col>
            {
              this.state.loggedIn ? 
              <Button shape="round" onClick={this.signoutOnClick}>Logout</Button> :
              (
                <>
                  <Login onSuccess={this.signinOnSuccess} />
                  <Register />
                </>
              )
            }
          </Col>
        </Row>
      </Header>
      <Layout>
        <Sider width={300} className="site-layout-background">
          <CustomSearch />
          <Menu mode="inline" onSelect={() => {}} style={{ marginTop: '10px' }}>
            <Menu.Item icon={<LikeOutlined />} key="Recommendation">Recommend for you!</Menu.Item>

            <SubMenu icon={<FireOutlined />} key="Popular Games" title="Popular Games" className="site-top-game-list" >
              {
                this.state.topGames.map((game) => {
                  return (
                    <Menu.Item key={game.id} style={{height : '50px'}}>
                      <img 
                        alt="Placeholder" 
                        src={game.box_art_url.replace('{height}', '40').replace('{width}', '40')}
                        style={{borderRadius: '50%', marginRight: '20px'}}/> 
                      <span>{game.name}</span>
                    </Menu.Item>
                  )
                })
              }
            </SubMenu>
          </Menu>
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

