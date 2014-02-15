package ch.retorte.intervalmusiccompositor.spi.messagebus;

import ch.retorte.intervalmusiccompositor.messagebus.Message;


/**
 * The {@link MessageHandler} is an agent which handles certain types of messages, denoted by the generic parameter.
 * 
 * @author nw
 */
public interface MessageHandler<T extends Message> {

  /**
   * Handles the received message.
   *
   * @param message the to be handled message.
   */
  void handle(T message);

}
