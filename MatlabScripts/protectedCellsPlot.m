function protectedCellsPlot()
% Create a plot with the number of cells protected

cd('../General');
H = load('green.txt');
G = grpstats(H, [H(:,1)]);
steps = [1:1:600];
plot(steps, G(1,2:end), steps, G(2,2:end),steps, G(3,2:end))
hold on;
xlabel('time steps')
ylabel('green cells')
title('Evolution of the green cells','FontSize',12)
legend('Random','GA','close','Location','NorthWest')
handle=gcf;
saveas(handle, ['greencells', 'eps'],'epsc')
hold off;
cd('../Functions');
end

