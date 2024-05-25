import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import fcfs.*;
import newScheduler.*;
import process.*;
import result.SchedulingResult;
import roundrobin.*;
import sjf.*;
import srtf.*;

public class Program extends JFrame {
    private final String[] inputHeaders = {"프로세스", "도착시간", "실행시간", "우선순위"};
    private final String[] outputHeaders = {"프로세스", "실행시간", "대기시간", "응답시간"};
    private JTable inputTable;
    private JTable outputTable;
    private JButton openFileBtn;
    private JButton fcfsBtn;
    private JButton newSchedulerBtn;
    private JButton sjfBtn;
    private JButton srtfBtn;
    private JButton rrBtn;
    private GanttChartPanel ganttChartPanel;

    private DefaultTableModel model1;
    private DefaultTableModel model2;

    private JLabel totalExecutionTimeLabel;
    private JLabel averageWaitingTimeLabel;

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            readProcessDataFromFile(selectedFile);
        }
    }

    private void readProcessDataFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            model1.setRowCount(0);
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 4) {
                    int pid = Integer.parseInt(parts[0].trim());
                    int arrivalTime = Integer.parseInt(parts[1].trim());
                    int duration = Integer.parseInt(parts[2].trim());
                    int priority = Integer.parseInt(parts[3].trim());
                    model1.addRow(new Object[]{pid, arrivalTime, duration, priority});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fcfsPerformed(ActionEvent e) {
        executeSchedulingAlgorithm("FCFS");
    }

    public void newSchedulerPerformed(ActionEvent e) {
        String timeSliceStr = JOptionPane.showInputDialog(this, "Time Slice:");
        if (timeSliceStr != null) {
            try {
                int timeSlice = Integer.parseInt(timeSliceStr.trim());
                executeSchedulingAlgorithm("NewScheduler", timeSlice);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "유효한 숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
        //executeSchedulingAlgorithm("NewScheduler");
    }

    public void sjfPerformed(ActionEvent e) {
        executeSchedulingAlgorithm("SJF");
    }

    public void srtfPerformed(ActionEvent e) {
        executeSchedulingAlgorithm("SRTF");
    }

    public void rrPerformed(ActionEvent e) {
        String timeSliceStr = JOptionPane.showInputDialog(this, "Time Slice:");
        if (timeSliceStr != null) {
            try {
                int timeSlice = Integer.parseInt(timeSliceStr.trim());
                executeSchedulingAlgorithm("RR", timeSlice);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "유효한 숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void executeSchedulingAlgorithm(String algorithm,int timeSlice) {
        int rowCount = model1.getRowCount();
        ArrayList<process> processes = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            int pid = (int) model1.getValueAt(i, 0);
            int arrivalTime = (int) model1.getValueAt(i, 1);
            int duration = (int) model1.getValueAt(i, 2);
            int priority = (int) model1.getValueAt(i, 3);
            processes.add(new process(pid, arrivalTime, duration, priority));
        }

        ganttChartPanel.clearProcesses();
        List<SchedulingResult> results = new ArrayList<>();

        switch (algorithm) {
            case "FCFS":
                results = fcfs.fcfs(processes);
                break;
            case "NewScheduler":
                results = newScheduler.newScheduler(processes, timeSlice);
                break;
            case "SJF":
                results = sjf.sjf(processes);
                break;
            case "SRTF":
                results = srtf.srtf(processes);
                break;
            case "RR":
                results = roundrobin.roundrobin(processes,timeSlice);
                break;
            default:
                // 다른 스케줄러에 대한 처리 추가
                break;

        }

        for (SchedulingResult result : results) {
            ganttChartPanel.addProcess("P" + result.getPid(), result.getStartTime(), result.getDuration());
        }
        ganttChartPanel.repaint();
        displayResults(results);
    }

    String timeSliceStr;
    private void executeSchedulingAlgorithm(String algorithm) {
        int timeSlice = 0; // 기본값 설정
        switch (algorithm) {
            case "RR":
                //String timeSliceStr = JOptionPane.showInputDialog(this, "Time Slice:");
                timeSliceStr = JOptionPane.showInputDialog(this, "Time Slice:");
                if (timeSliceStr != null) {
                    try {
                        timeSlice = Integer.parseInt(timeSliceStr.trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "유효한 숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            case "NewScheduler":
                //String timeSliceStr = JOptionPane.showInputDialog(this, "Time Slice:");
                timeSliceStr = JOptionPane.showInputDialog(this, "Time Slice:");
                if (timeSliceStr != null) {
                    try {
                        timeSlice = Integer.parseInt(timeSliceStr.trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "유효한 숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            default:
                // 다른 스케줄러의 경우 timeSlice 값을 사용하지 않으므로 0으로 설정
                break;
        }
        executeSchedulingAlgorithm(algorithm, timeSlice);
    }


    private void displayResults(List<SchedulingResult> results) {
        model2.setRowCount(0);

        if (results.isEmpty()) return;

        // 병합된 결과를 저장할 리스트
        List<SchedulingResult> mergedResults = new ArrayList<>();

        SchedulingResult lastResult = results.get(0);
        for (int i = 1; i < results.size(); i++) {
            SchedulingResult currentResult = results.get(i);

            if (currentResult.getPid() == lastResult.getPid() && currentResult.getStartTime() == lastResult.getStartTime() + lastResult.getDuration()) {
                // 연속된 실행 시간 병합
                lastResult = new SchedulingResult(
                        lastResult.getPid(),
                        lastResult.getStartTime(),
                        lastResult.getDuration() + currentResult.getDuration(),
                        lastResult.getWaitingTime(),
                        lastResult.getResponseTime()
                );
            } else {
                // 병합된 결과 추가
                mergedResults.add(lastResult);
                lastResult = currentResult;
            }
        }
        // 마지막 결과 추가
        mergedResults.add(lastResult);

        int totalExecutionTime = 0;
        int totalWaitingTime = 0;

        for (SchedulingResult result : mergedResults) {
            model2.addRow(new Object[]{"P" + result.getPid(), result.getDuration(), result.getWaitingTime(), result.getResponseTime()});
            totalExecutionTime = Math.max(totalExecutionTime, result.getStartTime() + result.getDuration());
            totalWaitingTime += result.getWaitingTime();
        }

        int processCount = mergedResults.size();
        double averageWaitingTime = processCount > 0 ? (double) totalWaitingTime / processCount : 0;

        totalExecutionTimeLabel.setText("전체 실행시간: " + totalExecutionTime);
        averageWaitingTimeLabel.setText("평균 대기시간: " + String.format("%.2f", averageWaitingTime));
    }

    class GanttChartPanel extends JPanel {
        private ArrayList<ProcessBlock> processes;

        public GanttChartPanel() {
            this.processes = new ArrayList<>();
        }

        public void addProcess(String name, int startTime, int duration) {
            if (!processes.isEmpty()) {
                ProcessBlock lastBlock = processes.get(processes.size() - 1);
                if (lastBlock.name.equals(name) && lastBlock.startTime + lastBlock.duration == startTime) {
                    // 연속되는 프로세스 실행 시간을 병합
                    lastBlock.duration += duration;
                    return;
                }
            }
            this.processes.add(new ProcessBlock(name, startTime, duration));
        }

        public void clearProcesses() {
            this.processes.clear();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int x = 10; // 초기 x 좌표

            for (ProcessBlock process : processes) {
                int width = process.duration * 20; // 프로세스 블록의 너비
                g.setColor(Color.YELLOW);
                g.fillRect(x, 30, width, 30); // 프로세스 블록 그리기
                g.setColor(Color.BLACK);
                g.drawRect(x, 30, width, 30); // 프로세스 블록 경계선 그리기
                g.drawString(process.name, x + 5, 50); // 프로세스 이름 표시

                // 프로세스 시작 시간 표시
                g.drawString(String.valueOf(process.startTime), x, 70);
                x += width; // 다음 프로세스 블록의 시작 x 좌표

                // 프로세스 종료 시간 표시
                g.drawString(String.valueOf(process.startTime + process.duration), x, 70);
            }
        }

        static class ProcessBlock {
            String name;
            int startTime;
            int duration;

            public ProcessBlock(String name, int startTime, int duration) {
                this.name = name;
                this.startTime = startTime;
                this.duration = duration;
            }
        }
    }

    public Program() {
        setTitle("CPU 스케줄러 프로그램");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        openFileBtn = new JButton("파일 열기");
        openFileBtn.addActionListener(e -> openFile());
        add(openFileBtn);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        JLabel inputLabel = new JLabel("입력");
        inputPanel.add(inputLabel, BorderLayout.NORTH);

        model1 = new DefaultTableModel(null, inputHeaders);
        inputTable = new JTable(model1);
        JScrollPane scrollPane1 = new JScrollPane(inputTable);
        scrollPane1.setPreferredSize(new Dimension(650, 200));
        inputPanel.add(scrollPane1, BorderLayout.CENTER);
        add(inputPanel);

        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());
        JLabel outputLabel = new JLabel("출력");
        outputPanel.add(outputLabel, BorderLayout.NORTH);

        model2 = new DefaultTableModel(null, outputHeaders);
        outputTable = new JTable(model2);
        JScrollPane scrollPane2 = new JScrollPane(outputTable);
        scrollPane2.setPreferredSize(new Dimension(650, 200));
        outputPanel.add(scrollPane2, BorderLayout.CENTER);
        add(outputPanel);

        fcfsBtn = new JButton("FCFS");
        fcfsBtn.addActionListener(e -> fcfsPerformed(e));
        add(fcfsBtn);

        newSchedulerBtn = new JButton("NewScheduler");
        newSchedulerBtn.addActionListener(e -> newSchedulerPerformed(e));
        add(newSchedulerBtn);

        sjfBtn = new JButton("SJF");
        sjfBtn.addActionListener(e -> sjfPerformed(e));
        add(sjfBtn);

        srtfBtn = new JButton("SRTF");
        srtfBtn.addActionListener(e -> srtfPerformed(e));
        add(srtfBtn);

        rrBtn = new JButton("RR");
        rrBtn.addActionListener(e -> rrPerformed(e));
        add(rrBtn);

        JPanel timeInfoPanel = new JPanel();
        timeInfoPanel.setLayout(new GridLayout(1, 2));

        totalExecutionTimeLabel = new JLabel("전체 실행시간: ");
        averageWaitingTimeLabel = new JLabel("평균 대기시간: ");
        timeInfoPanel.add(totalExecutionTimeLabel);
        timeInfoPanel.add(averageWaitingTimeLabel);
        add(timeInfoPanel);

        ganttChartPanel = new GanttChartPanel();
        ganttChartPanel.setPreferredSize(new Dimension(650, 100));
        add(ganttChartPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Program::new);
    }
}