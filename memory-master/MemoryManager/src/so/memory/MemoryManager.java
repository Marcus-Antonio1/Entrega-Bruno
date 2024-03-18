package so.memory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import so.Process;

public class MemoryManager {
	private String[] physicMemory;
	private Hashtable<String, List<FrameMemory>> logicalMemory;
	private int pageSize;
	private Strategy strategy;

	public MemoryManager(Strategy strategy, int pageSize) {
		physicMemory = new String[128];
		this.pageSize = pageSize;
		this.strategy = strategy;
		logicalMemory = new Hashtable<>();
	}

	public MemoryManager(Strategy strategy) {
		this(strategy, 2);
	}

	public void write(Process p) {
		if (strategy.equals(Strategy.FIRST_FIT)) {
			this.writeUsingFirstFit(p);
		} else if (strategy.equals(Strategy.BEST_FIT)) {
			this.writeUsingBestFit(p);
		} else if (strategy.equals(Strategy.WORST_FIT)) {
			this.writeUsingWorstFit(p);
		} else if (strategy.equals(Strategy.PAGING)) {
			this.writeUsingPaging(p);
		}
	}

	private void writeUsingPaging(Process p) {
		List<FrameMemory> frames = this.getFrames(p);
		if (frames == null) {
			System.out.println("NÃO TEM ESPAÇO");
		} else {
			for (int i = 0; i < frames.size(); i++) {
				FrameMemory actuallyFrame = frames.get(i);
				for (int j = actuallyFrame.getFrameNumber(); j < actuallyFrame.getOffset();) {
					this.physicMemory[j] = p.getId();
				}
			}

		}
		this.logicalMemory.put(p.getId(), frames);

	}

	private void delete (Process p) {
		List<FrameMemory> frames = this.logicalMemory.get(p.getId());
		for(int i = 0; i < frames.size(); i++) {
			FrameMemory actuallyFrame = frames.get(i);
			for (int j = actuallyFrame.getFrameNumber(); j < actuallyFrame.getOffset();) {
			this.physicMemory[j] =null;
		}
	}
		this.logicalMemory.remove(p.getId());
}

	private List<FrameMemory> getFrames(Process p) {
		List<FrameMemory> frames = new ArrayList<>();
		int increment = 0;
		for (int page = 0; page < this.physicMemory.length; page += this.pageSize) {
			if (this.physicMemory[page] == null) {
				int offset = page + this.pageSize;
				frames.add(new FrameMemory(page, offset));
				increment += this.pageSize;
				if (increment == p.getSizeInMemory()) {
					return frames;
				}

			}
		}
		return null;
	}

	private void writeUsingWorstFit(Process p) {
		// TODO Auto-generated method stub
	}

	private void writeUsingBestFit(Process p) {
		// TODO Auto-generated method stub

	}

	private void writeUsingFirstFit(Process p) {
		int actualSize = 0;
		
		for (int i = 0; i < physicMemory.length; i++) {
			if (i == (physicMemory.length - 1)) {

				if (actualSize > 0) {
					if (p.getSizeInMemory() <= actualSize) {
						int start = (i - actualSize);
						for (int j = start; j < start + p.getSizeInMemory(); j++) {
							physicMemory[j] = p.getId();

						}
						break;
					}

				}

			} else if (physicMemory[i] == null) {
				actualSize++;
			} else {
				if (actualSize > 0) {
					if (p.getSizeInMemory() <= actualSize) {
						int start = i - actualSize;
						for (int j = start; j < (start + p.getSizeInMemory()); i++) {
							physicMemory[j] = p.getId();

						}
						break;
					}

				}
				actualSize = 0;
			}
		}
		printMemoryStatus();
	}

	private void printMemoryStatus() {
		for (int i = 0; i < physicMemory.length; i++) {
			System.out.print(physicMemory[i] + " | ");
		}

	}

}
