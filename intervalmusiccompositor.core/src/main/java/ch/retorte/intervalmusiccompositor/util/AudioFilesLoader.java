package ch.retorte.intervalmusiccompositor.util;

import static ch.retorte.intervalmusiccompositor.commons.Utf8Bundle.getBundle;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.reverse;
import static java.util.Collections.sort;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ch.retorte.intervalmusiccompositor.audiofile.AudioFileFactory;
import ch.retorte.intervalmusiccompositor.commons.MessageFormatBundle;
import ch.retorte.intervalmusiccompositor.messagebus.DebugMessage;
import ch.retorte.intervalmusiccompositor.spi.MusicListControl;
import ch.retorte.intervalmusiccompositor.spi.TaskFinishListener;
import ch.retorte.intervalmusiccompositor.spi.messagebus.MessageProducer;

import com.google.common.annotations.VisibleForTesting;

/**
 * @author nw
 */
public class AudioFilesLoader implements Runnable {

  private MessageFormatBundle bundle = getBundle("core_imc");

  private MusicListControl musicListControl;
  private AudioFileFactory audioFileFactory;
  private MessageProducer messageProducer;
  private ArrayList<TaskFinishListener> taskFinishListeners = new ArrayList<TaskFinishListener>();

  public AudioFilesLoader(MusicListControl musicListControl, AudioFileFactory audioFileFactory, MessageProducer messageProducer) {
    this.musicListControl = musicListControl;
    this.audioFileFactory = audioFileFactory;
    this.messageProducer = messageProducer;
  }

  public void addListener(TaskFinishListener l) {
    taskFinishListeners.add(l);
  }

  private void notifyListeners() {
    for (TaskFinishListener l : taskFinishListeners) {
      l.onTaskFinished();
    }
  }

  @Override
  public void run() {
   loadAudioFiles();
  }

  @VisibleForTesting
  synchronized void loadAudioFiles() {
    List<File> allFiles = getAllFilesFromCurrentDirectory();
    List<File> audioFiles = keepKnownNewSoundFilesFrom(allFiles);

    // Reverse list since we insert them all at position 0.
    sort(audioFiles);
    reverse(audioFiles);

    for (File audioFile : audioFiles) {
      musicListControl.addMusicTrack(0, audioFile);
    }

    notifyListeners();
  }

  @VisibleForTesting
  List<File> getAllFilesFromCurrentDirectory() {
    List<File> result = newArrayList();

    File[] files = new File(getWorkPath()).listFiles();
    if (files == null) {
      messageProducer.send(new DebugMessage(this, "Problems with reading files from current directory: " + getWorkPath()));
      return result;
    }

    for (File f : files) {
      if (f.isFile()) {
        result.add(f);
      }
    }

    return asList(files);
  }

  @VisibleForTesting
  String getWorkPath() {
    return bundle.getString("imc.workPath");
  }

  @VisibleForTesting
  synchronized List<File> keepKnownNewSoundFilesFrom(List<File> files) {
    List<File> result = newArrayList();

    for (File file : files) {
      if (isNotSelfProduced(file) && audioFileFactory.hasDecoderFor(file)) {
        result.add(file);
      }
    }

    return result;
  }

  @VisibleForTesting
  boolean isNotSelfProduced(File file) {
    return !file.getName().contains(getOwnFileInfix());
  }

  @VisibleForTesting
  String getOwnFileInfix() {
    return bundle.getString("imc.outfile.sound.infix");
  }

}
