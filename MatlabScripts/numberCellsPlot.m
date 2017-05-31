function numberCellsPlot()
% Create a plot with the behaviour of the cells

cd('../General');
M = load('Cells.txt');
steps = [1:1:600];
plot(steps, M(:,1), steps, M(:,2),steps, M(:,3),steps, M(:,4),steps, M(:,5), 'LineWidth',3)
hold on;
xlabel('time steps')
ylabel('urban growth')
axis([0,600,-100,2600])
title('Evolution of the cells', 'fontweight','bold', 'FontSize',14)
legend('total capacity','rural cells','available cells','new cells','old cells','Location','northoutside','Orientation','horizontal')
handle=gcf;
saveas(handle, ['cells', 'eps'],'epsc')
hold off;
cd('../Functions');
end

