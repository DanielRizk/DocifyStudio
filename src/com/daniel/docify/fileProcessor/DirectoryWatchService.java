package com.daniel.docify.fileProcessor;

import com.daniel.docify.model.FileNodeModel;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryWatchService implements Runnable{
    private final Map<Path, WatchKey> directoriesBeingWatched = new ConcurrentHashMap<>();
    private final WatchService watchService;
    private final FileNodeModel rootNode;
    private final DirectoryProcessor processor;
    private static Thread watchThread;
    private static volatile boolean running = true;

    public DirectoryWatchService(Path rootPath, FileNodeModel node, DirectoryProcessor processor) throws IOException {
        this.rootNode = node;
        this.processor = processor;
        this.watchService = FileSystems.getDefault().newWatchService();
        registerAll(rootPath);
    }

    public static void syncOn(FileNodeModel node, DirectoryProcessor processor) throws IOException {
        DirectoryWatchService service = new DirectoryWatchService(Path.of(node.getFullPath()), node, processor);
        Thread thread = new Thread(service, "Watch Service Thread");
        watchThread = thread;
        running = true;
        thread.start();
    }

    private void registerDirectory(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        directoriesBeingWatched.put(dir, key);
    }

    // Method to register all directories (root and subdirectories)
    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException x) {
                if (!running) {
                    Thread.currentThread().interrupt();
                    return;
                }
                continue;
            }

            Path dir = (Path) key.watchable();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path affectedPath = ((WatchEvent<Path>) event).context();
                Path fullPath = dir.resolve(affectedPath);

                boolean isFile = Files.exists(fullPath) && !Files.isDirectory(fullPath);


                if (kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(fullPath)) {
                            registerAll(fullPath);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Could not register dir: " + fullPath);
                    }
                    System.out.println("Directory added to watch: " + dir + ". Event kind: " + kind + ". File affected: " + fullPath + ".");
                }

                else if (kind == ENTRY_DELETE) {
                    // Assuming 'directoriesBeingWatched' is a Map<Path, WatchKey>
                    WatchKey keyOfDeletedDir = directoriesBeingWatched.get(fullPath);

                    if (keyOfDeletedDir != null) {
                        keyOfDeletedDir.cancel(); // Cancel the watch key
                        directoriesBeingWatched.remove(fullPath); // Remove the entry from the map

                        // Update your directory tree or other data structures
                        //removeDirectoryFromTree(fullPath);
                        System.out.println("Directory removed from monitoring: " + dir + ". Event kind: " + kind + ". File affected: " + fullPath + ".");
                    }
                }else {
                    System.out.println("Directory Modified: " + dir + ". Event kind: " + kind + ". File affected: " + fullPath + ".");
                }
                try {
                    if (!fullPath.toString().endsWith(".ignore") && !fullPath.toString().endsWith(".doci")) {
                        rootNode.updateNode(dir, isFile, kind, processor);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // Key reset and validity check
            boolean valid = key.reset();
            if (!valid) {
                directoriesBeingWatched.remove(dir); // Remove from tracking if the directory is no longer accessible
                if (directoriesBeingWatched.isEmpty()) {
                    break; // All directories are inaccessible
                }
            }
        }
    }

    public static void stop() {
        running = false;
        if (watchThread != null && watchThread.isAlive()) {
            watchThread.interrupt();
        }
    }
}
