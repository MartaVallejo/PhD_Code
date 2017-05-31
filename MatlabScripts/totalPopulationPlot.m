function totalPopulationPlot()
% Create a plot with the evolution of the population (young, mature, old)

cd('../General');
U = load('Cells.txt');
steps = [1:1:600];
plot(steps, U(:,6), steps, U(:,7),steps, U(:,8), 'LineWidth',3)
hold on;
xlabel('time steps')
ylabel('type of agents')
title('Evolution of the Urban Population', 'fontweight','bold', 'FontSize',14)
legend('old','mature','young','Location','northoutside','Orientation','horizontal')
handle=gcf;
saveas(handle, 'populationAge','epsc')
hold off;
cd('../Functions');
end

