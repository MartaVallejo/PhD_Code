function populationPlot()
% Create a plot with the population behaviour
cd('../General');
H = load('population.txt');
P = grpstats(H, [H(:,1)]);
steps = [1:1:600];
h=plot(steps, P(1,2:end), steps, P(2,2:end),steps, P(3,2:end));
set (h, 'LineWidth', 2)
hold on;
xlabel('Time Steps','fontweight','bold','fontsize',14)
ylabel('Population Size','fontweight','bold','fontsize',14)
title('Evolution of the population','fontweight','bold','fontsize',16)
legend('RAN','GA','CLO','Location','NorthWest')
handle=gcf;
%saveas(handle, ['population', 'png'],'png')
saveas(handle, 'population','epsc')
hold off;
cd('../Functions');
end

