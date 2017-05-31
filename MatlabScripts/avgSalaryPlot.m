function avgSalaryPlot()
% Create a plot with the evolution of the salary

cd('../General');
U = load('Cells.txt');
steps = [1:1:600];
plot(steps, U(:,9), 'LineWidth',3)
hold on;
xlabel('time steps')
ylabel('salary')
title('Evolution of the salary', 'fontweight','bold', 'FontSize',14)
%legend('salary','Location','NorthWest')
handle=gcf;
saveas(handle, 'avgSalary','epsc')
hold off;
cd('../Functions');
end

