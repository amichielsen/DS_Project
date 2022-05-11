package be.uantwerpen.node.lifeCycle.running.services;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class LocalFolderWatchdog extends Thread {

    private String path;

    public LocalFolderWatchdog(String path) {
        this.path = path;
    }

    public void run() {
        WatchService watcher = null;
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // wait for key to be signaled
        Path dir = null;
        dir = Path.of(this.path);
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
                System.out.println("exception");
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

                if (ev.kind() == ENTRY_DELETE) {
                    System.out.format("File Deteleted %s%n", filename);
                }
                else{
                    System.out.format("New file %s%n", filename);

                    // Run replication service
                    ReplicationService replicationService = new ReplicationService(Path.of(this.path + "/" + filename));
                    replicationService.start();
                }
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
