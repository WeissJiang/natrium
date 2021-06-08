import styled from 'styled-components'

const ErrorButton = styled.button`
  box-sizing: border-box;
  text-decoration: inherit;
  margin: 0 .25rem;
  padding: .125rem .5rem;
  font-weight: 500;
  font-size: .875rem;
  line-height: 1.25rem;
  border: none;
  border-radius: 2px;
  background-color: transparent;
  cursor: pointer;
  color: #ef4444;
  transition: color, background-color 200ms;

  &:hover {
    color: #fff;
    background-color: #ef4444;
  }
`

export default ErrorButton