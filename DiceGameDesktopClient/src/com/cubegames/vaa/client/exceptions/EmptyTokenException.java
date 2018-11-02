package com.cubegames.vaa.client.exceptions;

public class EmptyTokenException extends ClientException {

  public EmptyTokenException() {
    super("Token is empty");
  }

}
