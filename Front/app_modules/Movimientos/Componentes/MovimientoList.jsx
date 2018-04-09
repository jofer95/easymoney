import React from 'react';
import ReactDOM from 'react-dom';
import { Menu, Icon, Header, Table, Dimmer, Loader, Segment, Container, Modal, Button, Divider, Form, Input, Pagination} from 'semantic-ui-react'
import MovimientoForm from './MovimientoForm.jsx'
import * as utils from '../../../utils.js';

export default class MovimientoList extends React.Component {

  constructor(props){
    super(props);
    this.state = {
      movimientos: [],
      conMovimientos: true,
      activePage:1,
      totalPages:10,
      modalOpenAgregar: false,
      modalOpenWarning: false,
      buscando: false,
      filtro:{
        nombreCobrador:'',
        fechaFinal: '',
        fechaInicial: ''
      }
    }
    this.handleCloseAgregar = this.handleCloseAgregar.bind(this);
    this.handleOpenAgregar = this.handleOpenAgregar.bind(this);
    this.handlePaginationChange = this.handlePaginationChange.bind(this);
  }

  handlePaginationChange(e, { activePage }){
     this.setState({ activePage });
  }

  handleCloseAgregar(hasChanges){
    this.setState({modalOpenAgregar: false});
    if (hasChanges.hasChanges) {
        this.cargarMovimientos();
    }
  }

  handleOpenAgregar(){
    this.setState({modalOpenAgregar: true});
  }

  cargarMovimientos(){
    let {filtro} = this.state;
    let fechaInicial = filtro.fechaInicial !== '' ? utils.toUtcDate(filtro.fechaInicial) : '';
    let fechaFinal = filtro.fechaFinal !== '' ? utils.toUtcDate(filtro.fechaFinal) + 86400000  : '';
      fetch(localStorage.getItem('url') + 'movimientos/filtro',{
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin':'*',
          'Authorization': localStorage.getItem('tokenSesion')
        },
        body: JSON.stringify({
          nombre: filtro.nombreCobrador,
          fechaInicial,
          fechaFinal
        })
      }).then((res)=> res.json())
      .then((response) =>{
        utils.evalResponse(response, ()=>{
          if (response.data.length > 0) {
            let totalPages = Math.ceil(response.data.length / 10);
            this.setState({
              movimientos: response.data,
              conMovimientos:true,
              buscando:false,
              totalPages
            });
          }else{
            this.setState({conMovimientos: false, buscando:false});
          }
        })
      })
  }

  renderMovimientoList(){
    const limiteSuperior = this.state.activePage * 10;
    let movs = this.state.movimientos.slice(limiteSuperior - 10, limiteSuperior);
    return movs.map((ie) =>{
      return(
        <Table.Row key={ie.id}>
          <Table.Cell>
            <Header textAlign='center'>
              {ie.id}
            </Header>
          </Table.Cell>
          <Table.Cell>
            {new Date(ie.fecha).toLocaleDateString()}
          </Table.Cell>
          <Table.Cell>
            {ie.usuarioCreador.nombre}
          </Table.Cell>
          <Table.Cell>
            {ie.cantidad}
          </Table.Cell>
          <Table.Cell>
            {ie.descripcion}
          </Table.Cell>
        </Table.Row>
      )
    })
  }

  renderMovimientos(){
    if (this.state.conMovimientos) {
        return(
          <div>
          <Table celled selectable striped>
            <Table.Header>
              <Table.Row>
                <Table.HeaderCell textAlign='center'>Id</Table.HeaderCell>
                <Table.HeaderCell>Fecha</Table.HeaderCell>
                <Table.HeaderCell>Usuario</Table.HeaderCell>
                <Table.HeaderCell>Monto</Table.HeaderCell>
                <Table.HeaderCell>Descripción</Table.HeaderCell>
              </Table.Row>
            </Table.Header>
            <Table.Body>
              {this.renderMovimientoList()}
            </Table.Body>
          </Table>
          <Pagination activePage={this.state.activePage} onPageChange={this.handlePaginationChange} totalPages={this.state.totalPages} />
          </div>
        )
    }else{
      return(
        <Container textAlign='center'>
            <h2>Sin Movimientos...</h2>
        </Container>
      );
    }
  }

  renderButtonBuscar(){
    if (this.state.buscando) {
      return(
        <Button primary loading>Buscar</Button>
      );
    }else{
      return(
        <Button primary type='submit' onClick={()=>{
            this.setState({buscando:true})
            this.cargarMovimientos();
        }}>Buscar</Button>
      );
    }
  }

  renderFiltros(){
    return(
      <Form>
        <Form.Group>
          <Form.Field
             control={Input}
             label='Nombre Cobrador:'
             type='text'
             placeholder='Nombre de cobrador'
             value={this.state.filtro.nombreCobrador}
             onChange={ (evt) => {
               let {filtro} = this.state;
               filtro.nombreCobrador = evt.target.value;
               this.setState({filtro});
             }}/>
          <Form.Field>
            <label>Movimientos despues de:</label>
            <input
              type={'date'}
              value={this.state.filtro.fechaInicial}
              onChange={(evt) => {
                let {filtro} = this.state;
                filtro.fechaInicial = evt.target.value;
                this.setState({filtro});
              }}/>
          </Form.Field>
          <Form.Field>
            <label>Movimientos antes de:</label>
            <input
              type={'date'}
              value={this.state.filtro.fechaFinal}
              onChange={(evt) => {
                let {filtro} = this.state;
                filtro.fechaFinal = evt.target.value;
                this.setState({filtro});
              }}/>
          </Form.Field>
       </Form.Group>

       <Form.Group>
        <Form.Field>
          {this.renderButtonBuscar()}
        </Form.Field>

        <Form.Field control={Button} secundary onClick={ () => {
          let filtro = {
            nombreCobrador:'',
            fechaFinal:'',
            fechaInicial: ''
          }
          this.setState({filtro});
        }}>Limpiar filtros</Form.Field>

        <Modal trigger={<Button color='green' onClick={this.handleOpenAgregar}>Agregar</Button>}
          onClose={this.handleCloseAgregar}
          open={this.state.modalOpenAgregar}>
          <Header content='Agregar Ingreso o Egreso' />
          <Modal.Content>
            <MovimientoForm handleClose={this.handleCloseAgregar}/>
          </Modal.Content>
        </Modal>
        </Form.Group>
      </Form>
    );
  }

  render() {
    return (
      <div>
      <Segment>
        <Divider horizontal>Filtros</Divider>
        {this.renderFiltros()}
      </Segment>
        <Segment>
          {this.renderMovimientos()}
        </Segment>
      </div>
    )
  }

}
