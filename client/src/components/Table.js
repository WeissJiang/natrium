import styled from 'styled-components'

const Table = styled.table`
  box-sizing: border-box;
  border: 1px solid #000;
  border-collapse: collapse;

  & td, & th {
    padding: .5rem .5rem;
    border: 1px solid #000;
  }

  & tr.mark {
    background-color: #e4e4e7;
  }
`

export default Table