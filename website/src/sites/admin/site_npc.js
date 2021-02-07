import React from 'react';
import ReactDOM from 'react-dom';
import Coord_Span from "../general/Coord_Span";

const formater = require('../../tools/formater');
const coockie = require('../../tools/coockies');

class NPC_row extends React.Component {

    constructor(probs) {
        super(probs);
    }


    render() {
        return (
            
            <tr>
                <td>
                    <a href={'http://0.0.0.0:3000/admin/npc/' + this.props.id}>
                        {this.props.id} 
                    </a>
                </td>

                <td>
                    <a href={'http://0.0.0.0:3000/admin/npc/' + this.props.id}>
                        {this.props.name}
                    </a>
                </td>

                <td>                    
                    <Coord_Span pos={this.props.pos}/>
                </td>
            </tr>
            
        );
    }
}

class NPC_table extends React.Component {

    constructor(probs) {
        super(probs);

        this.state = {npcs: <span></span>}
    }

    componentDidMount(){
        this.ticker = setInterval(() => {this.createTable()}, 500);        
    }    

    async createTable() {
        const session = coockie.readCookie('login');
        let url = "http://0.0.0.0:10100/admin/" + session;
        let response = await fetch(url, { mode: 'cors', headers: { 'Access-Control-Allow-Origin': '*' } });
        let data = await response.json();       


        if (data.verified) {
            url = "http://0.0.0.0:10100/admin/" + session + "/npc/getall";
            response = await fetch(url, { mode: 'cors', headers: { 'Access-Control-Allow-Origin': '*' } });
            data = await response.json();  

            let table = [];
            data.npc.forEach(element => {
                table.push(<NPC_row id={element.ID} name={element.NAME} pos={element.POS} />);
            });         
            this.setState({npcs: table});
                
        } else {
            this.forceUpdate()

            coockie.deleteCoockie("login");
            this.setState({npcs: <span></span>});
        }

        this.forceUpdate()
    }

    render() {
        return (
            <table class='NPC_Table'>                
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Position</th>
                </tr>
                {this.state.npcs}
            </table>
        );
    }
}

class Site_NPC extends React.Component {
    constructor(probs) {
        super(probs);
    }

    render() {
        return (
            <div class='Site_NPC'>
                <NPC_table />
            </div>
        );
    }

}

export default Site_NPC