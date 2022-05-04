package be.uantwerpen.node.lifeCycle.running.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class FolderWatchdog extends Thread {
    public void run() {
        WatchService watcher = null;
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // wait for key to be signaled
        Path dir = null;
        dir = Path.of("/root/data/");
        System.out.println(dir);
        try {
            WatchKey key = dir.register(watcher,
                    ENTRY_CREATE,
                    ENTRY_DELETE,
                    ENTRY_MODIFY);
        } catch (IOException x) {
            System.err.println(x);
        }
        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                System.out.println("exjeption");
                return;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }

                // The filename is the
                // context of the event.
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();


                System.out.format("New file %s%n", filename);

                // Run replication service
                ReplicationService replicationService = new ReplicationService(filename);
                replicationService.start();
            }

            // Reset the key -- this step is critical if you want to
            // receive further watch events.  If the key is no longer valid,
            // the directory is inaccessible so exit the loop.
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }
}
