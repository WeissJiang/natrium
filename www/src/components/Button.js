import styled from 'styled-components'

const Button = styled.button`
  box-sizing: border-box;
  background-color: #f8f9fa;
  border: 1px solid #f8f9fa;
  border-radius: 4px;
  color: #3c4043;
  font-size: 14px;
  padding: 0 16px;
  line-height: 27px;
  height: 36px;
  min-width: 54px;
  text-align: center;
  cursor: pointer;
  user-select: none;
  white-space: nowrap;

  &:hover {
    box-shadow: 0 1px 1px rgba(0, 0, 0, .1);
    background-color: #f8f9fa;
    border: 1px solid #dadce0;
    color: #202124;
  }

  &:focus {
    border: 1px solid #4285f4;
    outline: none;
  }
`


export default Button