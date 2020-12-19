import React from 'react';
import { Button, Card, List, Tabs, Tooltip } from 'antd';
import { StarOutlined } from '@ant-design/icons';

const { TabPane } = Tabs;
const tabKeys = {
  Streams: 'stream',
  Videos: 'videos',
  Clips: 'clips',
}
 
const processUrl = (url) => url
  .replace('%{height}', '252')
  .replace('%{width}', '480')
  .replace('{height}', '252')
  .replace('{width}', '480');

const renderCardTitle = (item, loggedIn) => {
    const title = `${item.broadcaster_name} - ${item.title}`;
   
    return (
        <>
            {
                loggedIn &&
                <Tooltip title="Add to favorite list">
                    <Button shape="circle" icon={<StarOutlined />} />
                </Tooltip>
            }
            <div style={{ overflow: 'hidden', textOverflow: 'ellipsis', width: 450 }}>
            <Tooltip title={title}>
                <span>{title}</span>
            </Tooltip>
            </div>
        </>
    )
}

const renderCardGrid = (data, loggedIn) => {
    return (
        <List
            grid={{
                xs: 1,
                sm: 2,
                md: 4,
                lg: 4,
                xl: 6,
            }}
            dataSource={data}
            renderItem={item => (
                <List.Item style={{ marginRight: '20px' }}>
                    <Card title={renderCardTitle(item, loggedIn)}>
                    <a href={item.url} target="_blank" rel="noopener noreferrer">
                        <img alt="Placeholder" src={processUrl(item.thumbnail_url)}/>
                    </a>
                    </Card>
                </List.Item>
            )}
        />
    )
}
   
const Home = ({ resources, loggedIn }) => {
    const { VIDEO, STREAM, CLIP } = resources;
   
    return (
        <Tabs defaultActiveKey={tabKeys.Streams}>
            <TabPane tab="Streams" key={tabKeys.Streams} style={{ height: '680px', overflow: 'auto' }} forceRender={true}>
            {renderCardGrid(STREAM, loggedIn)}
            </TabPane>

            <TabPane tab="Videos" key={tabKeys.Videos} style={{ height: '680px', overflow: 'auto' }} forceRender={true}>
            {renderCardGrid(VIDEO, loggedIn)}
            </TabPane>

            <TabPane tab="Clips" key={tabKeys.Clips} style={{ height: '680px', overflow: 'auto' }} forceRender={true}>
            {renderCardGrid(CLIP, loggedIn)}
            </TabPane>
        </Tabs>
    );
}
   
export default Home;
  