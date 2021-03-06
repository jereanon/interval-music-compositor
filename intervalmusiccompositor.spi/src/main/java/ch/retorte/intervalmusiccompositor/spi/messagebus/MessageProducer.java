package ch.retorte.intervalmusiccompositor.spi.messagebus;

import ch.retorte.intervalmusiccompositor.messagebus.Message;


/**
 * The {@link MessageProducer} is the part of the message bus which feed it with new messages.
 * 
 * @author nw
 */
public interface MessageProducer {

  /**
   * Sends a message into the bus.
   */
  void send(Message message);
}
