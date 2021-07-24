import React from 'react'
import styled from 'styled-components'

const ModalContainer = styled.div`
  box-sizing: border-box;
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, .2);
  z-index: 100;
`

const ModalContent = styled.div`
  box-sizing: border-box;
  max-width: 400px;
  min-height: 300px;
  border-radius: 2px;
  margin: 20vh auto 0;
  background-color: #fff;
`

const ModalHeader = styled.div`
  box-sizing: border-box;
  padding: 1rem;
  border-bottom: 1px solid #eee;
  font-size: 1.125rem;
  line-height: 1.75rem;
  font-weight: 500;
`

const ModalBody = styled.div`
  box-sizing: border-box;
  padding: 1rem;
`

const ModalFooter = styled.div`
  box-sizing: border-box;
  padding: 1rem;
`

export default function Modal(props) {
    return (
        <ModalContainer>
            <ModalContent>
                <ModalHeader>
                    {props.title}
                </ModalHeader>
                <ModalBody>
                    {props.content}
                </ModalBody>
                <ModalFooter>
                    {props.footer}
                </ModalFooter>
            </ModalContent>
        </ModalContainer>
    )
}