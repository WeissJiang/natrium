import styled from 'styled-components'

const Table = styled.table`
  width: calc(100% - 2rem);
  margin: 1rem;
  box-sizing: border-box;
  border: 1px solid #000;
  border-collapse: collapse;

  & td, & th {
    padding: .5rem .5rem;
    border: 1px solid #000;
  }

`

export default Table