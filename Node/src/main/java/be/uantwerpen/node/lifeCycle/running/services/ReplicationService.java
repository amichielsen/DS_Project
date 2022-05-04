package be.uantwerpen.node.lifeCycle.running.services;

public class ReplicationService extends Thread {
/*
  1. User adds file to directory by using a tcp socket
  2. The watchdog will constantly be looking inside that directory
  3. When a new file is detected there are 3 options
    a. The file will need to stay at this node and will be the MAIN
    b. The file is lower than our id and needs to go to our previous node -> directly send
    c. The file is neither, and we will contact the NS to ask it about the correct Node. -> send after we know ip
  4. IF a failure occurs -> data has to be send to new neighboring node
  5. IF a shutdown occurs -> data has to be send to previous node


  - TCP sockets - lexiflexie superRTOS 2000
  - Structuur van data (L of R met dan id van plek + naam bestand) - Vital
  - Watchdog (nieuwe bestanden toegevoegd?) - Asif
  - Aangeroepen code die bekijkt naar waar alles moet - Louis

 */

}
