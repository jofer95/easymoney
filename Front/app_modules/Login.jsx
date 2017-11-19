import React from 'react';
import ReactDOM from 'react-dom';
import { Button, Segment, Divider, Header, Input, Form, Container} from 'semantic-ui-react'

export default class Login extends React.Component{

  constructor(props){
    super(props);

    this.state = {
      user: '',
      pass: '',
      message: ''
    }

    localStorage.setItem('tokenSesion', '');
    this.handleSumbit = this.handleSumbit.bind(this);
    this.handleUserChange = this.handleUserChange.bind(this);
    this.handlePassChange = this.handlePassChange.bind(this);

    let ruta = window.location.href.split('#');
    window.location.href = ruta[0] + '#/login';
  }

  handleSumbit(){
    fetch(localStorage.getItem('url') + 'accesos/login', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        user: this.state.user,
        pass: this.state.pass,
      })
    }).then((res) => res.json())
    .then((response) => {      
      if (response.meta.status == 'OK') {
        localStorage.setItem('tokenSesion', response.meta.metaData);
        let ruta = window.location.href.split('#');
        window.location.href = ruta[0] + '#/prestamos';
      }else{
        if (response.meta.status == 'WARNING') {
          this.setState({message: response.meta.message});
        }
      }
    })
  }

  handleUserChange(evt){
    this.setState({user: evt.target.value});
  }

  handlePassChange(evt){
    this.setState({pass: evt.target.value});
  }

  renderMessage(){
    if (this.state.message !== '') {
      return (
        <Segment color='yellow'>
          {this.state.message}
        </Segment>
      )
    }
  }

  render(){
    return(
      <div>
        <Segment textAlign='center'>
            <h1>Easy Money</h1>
        </Segment>
        <Container text>
          <Segment>
            <Header color='blue'>Iniciar sesion</Header>
            <Divider section/>
              {this.renderMessage()}
              <Form onSubmit={this.handleSumbit}>
                <Form.Field>
                  <label>Usuario:</label>
                  <input type='text' placeholder='ingrese el usuario...:'onChange={this.handleUserChange} />
                </Form.Field>
                <Form.Field>
                  <label>Contraseña:</label>
                  <input type='password' placeholder='ingrese la contraseña...' onChange={this.handlePassChange}/>
                </Form.Field>
                <Button primary type='sumbit'>Iniciar Sesion</Button>
              </Form>
          </Segment>
        </Container>
      </div>
    )
  }
}
